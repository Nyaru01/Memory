param(
    [Parameter(Mandatory = $true)][string]$Notes,
    [string[]]$BaseApk = @(),
    [string]$ToolRoot = 'D:\AndroidDev'
)
$ErrorActionPreference = 'Stop'
if ([IO.Path]::GetPathRoot([IO.Path]::GetFullPath($ToolRoot)) -ne 'D:\') { throw 'Les outils doivent rester sur D:.' }
$project = Split-Path $PSScriptRoot -Parent
$python = Join-Path $ToolRoot 'python\python.exe'
if (!(Test-Path -LiteralPath $python)) { throw "Python standard est requis sur D: : $python" }
if (!(Test-Path -LiteralPath $Notes)) { throw 'Notes de publication introuvables.' }
$variables = @('TEMP','TMP','PYTHONPYCACHEPREFIX','JAVA_HOME','JAVA_TOOL_OPTIONS')
$previous = @{}
foreach ($name in $variables) { $previous[$name] = [Environment]::GetEnvironmentVariable($name, 'Process') }
try {
    $env:TEMP = Join-Path $ToolRoot 'tmp'; $env:TMP = $env:TEMP
    $env:PYTHONPYCACHEPREFIX = Join-Path $env:TEMP 'python-cache'
    & "$PSScriptRoot\build.ps1" -ToolRoot $ToolRoot
    $apk = Join-Path $project 'APK\MemoMondes.apk'
    $env:JAVA_HOME = (Get-ChildItem (Join-Path $ToolRoot 'java') -Directory | Select-Object -First 1).FullName
    $env:JAVA_TOOL_OPTIONS = '-Duser.home=D:/AndroidDev/java-user -Djava.io.tmpdir=D:/AndroidDev/tmp'
    & (Join-Path $ToolRoot 'sdk\build-tools\35.0.0\apksigner.bat') verify $apk
    if ($LASTEXITCODE -ne 0) { throw 'Signature APK invalide.' }
    $badging = & (Join-Path $ToolRoot 'sdk\build-tools\35.0.0\aapt.exe') dump badging $apk
    $package = $badging | Where-Object { $_ -like 'package:*' } | Select-Object -First 1
    if ($package -notmatch "versionCode='(\d+)' versionName='([\d.]+)'" -or $package -notlike "*name='fr.memomimi.app'*") { throw 'Identité/version APK inconnue.' }
    $code = $Matches[1]; $version = $Matches[2]
    $output = Join-Path $project "app\build\github-release-$version"
    if (Test-Path -LiteralPath $output) { throw 'Dossier de publication déjà existant : examiner cette tentative avant de recommencer.' }
    $bases = Join-Path $project 'app\build\update-bases'
    New-Item -ItemType Directory -Force $output,$bases | Out-Null
    $baseArguments = @()
    foreach ($path in $BaseApk) { $baseArguments += @('--base', [IO.Path]::GetFullPath($path)) }
    $headers = @{ 'User-Agent' = 'MemoMondes-Release'; 'Accept' = 'application/vnd.github+json' }
    $releases = Invoke-RestMethod -Uri 'https://api.github.com/repos/Nyaru01/Memory/releases?per_page=100' -Headers $headers
    foreach ($release in $releases) {
        if ($release.draft -or $release.prerelease -or $release.tag_name -notmatch '^v[0-9.]+$') { continue }
        if ($release.tag_name -eq "v$version") { throw 'Cette version est déjà publiée. Augmenter versionCode et versionName.' }
        $asset = $release.assets | Where-Object { $_.name -eq 'MemoMondes.apk' } | Select-Object -First 1
        if (!$asset) { continue }
        $url = [Uri]$asset.browser_download_url
        if ($url.Scheme -ne 'https' -or $url.Host -ne 'github.com' -or $url.AbsolutePath -notlike '/Nyaru01/Memory/releases/download/*' -or $asset.size -gt 268435456) { throw 'Ancien APK non fiable.' }
        $base = Join-Path $bases "$($release.tag_name).apk"
        Invoke-WebRequest -Uri $url.AbsoluteUri -OutFile $base -Headers @{ 'User-Agent' = 'MemoMondes-Release' }
        $baseArguments += @('--base', $base)
    }
    & $python "$PSScriptRoot\apk_delta.py" @baseArguments --target $apk --output $output --version $version --code $code
    if ($LASTEXITCODE -ne 0) { throw 'Génération ou reconstruction des correctifs en échec.' }
    & $python "$PSScriptRoot\github_release.py" --directory $output --apk $apk --notes ([IO.Path]::GetFullPath($Notes))
    if ($LASTEXITCODE -ne 0) { throw 'Publication GitHub en échec ; la dernière release reste active.' }
} finally {
    foreach ($name in $variables) { [Environment]::SetEnvironmentVariable($name, $previous[$name], 'Process') }
}
