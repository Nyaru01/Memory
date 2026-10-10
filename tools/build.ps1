param(
    [string]$ToolRoot = 'D:\AndroidDev',
    [string[]]$Tasks = @(':app:testDebugUnitTest', ':app:assembleRelease', ':app:lintRelease')
)
$ErrorActionPreference = 'Stop'
if ([IO.Path]::GetPathRoot([IO.Path]::GetFullPath($ToolRoot)) -ne 'D:\') {
    throw 'Les outils et caches doivent rester sur D:.'
}
$jdk = Get-ChildItem (Join-Path $ToolRoot 'java') -Directory |
    Where-Object { Test-Path (Join-Path $_.FullName 'bin\java.exe') } |
    Select-Object -First 1
if (!$jdk) { throw "JDK introuvable dans $ToolRoot\java" }
if (!(Test-Path -LiteralPath 'D:\AndroidDev\signing\MemoMondes.keystore')) {
    throw 'Clé de signature fixe introuvable : D:\AndroidDev\signing\MemoMondes.keystore. Restaurer la clé conservée, sans en générer une nouvelle.'
}
$variables = @('JAVA_HOME', 'ANDROID_HOME', 'ANDROID_USER_HOME', 'ANDROID_EMULATOR_HOME', 'ANDROID_AVD_HOME', 'ANDROID_SDK_HOME', 'GRADLE_USER_HOME', 'TEMP', 'TMP', 'JAVA_TOOL_OPTIONS')
$previous = @{}
foreach ($name in $variables) { $previous[$name] = [Environment]::GetEnvironmentVariable($name, 'Process') }
try {
    $env:JAVA_HOME = $jdk.FullName
    $env:ANDROID_HOME = Join-Path $ToolRoot 'sdk'
    $env:ANDROID_USER_HOME = Join-Path $ToolRoot 'android-user'
    $env:ANDROID_EMULATOR_HOME = Join-Path $ToolRoot 'android-emulator'
    $env:ANDROID_AVD_HOME = Join-Path $ToolRoot 'avd'
    # Older Android preference variables conflict with ANDROID_USER_HOME in AGP.
    $env:ANDROID_SDK_HOME = $null
    $env:GRADLE_USER_HOME = Join-Path $ToolRoot 'gradle'
    $env:TEMP = Join-Path $ToolRoot 'tmp'
    $env:TMP = $env:TEMP
    New-Item -ItemType Directory -Force $env:TEMP, $env:ANDROID_USER_HOME, $env:GRADLE_USER_HOME | Out-Null
    $javaUser = (Join-Path $ToolRoot 'java-user').Replace('\', '/')
    $javaTemp = $env:TEMP.Replace('\', '/')
    $env:JAVA_TOOL_OPTIONS = "$($previous['JAVA_TOOL_OPTIONS']) -Duser.home=`"$javaUser`" -Djava.io.tmpdir=`"$javaTemp`""
    Push-Location (Split-Path $PSScriptRoot -Parent)
    try {
        & .\gradlew.bat --no-daemon @Tasks
        if ($LASTEXITCODE -ne 0) { throw "Gradle a échoué (code $LASTEXITCODE)." }
        if ($Tasks -contains ':app:assembleRelease') {
            & "$PSScriptRoot\publish-apk.ps1"
        }
    } finally { Pop-Location }
} finally {
    foreach ($name in $variables) { [Environment]::SetEnvironmentVariable($name, $previous[$name], 'Process') }
}
