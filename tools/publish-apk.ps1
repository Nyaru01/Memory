param([string]$Source = '')
$ErrorActionPreference = 'Stop'
$project = Split-Path $PSScriptRoot -Parent
if (!$Source) { $Source = Join-Path $project 'app\build\outputs\apk\release\app-release.apk' }
if (!(Test-Path -LiteralPath $Source -PathType Leaf)) { throw 'APK source introuvable.' }
$destination = Join-Path $project 'APK'
New-Item -ItemType Directory -Force $destination | Out-Null
$final = Join-Path $destination 'MemoMondes.apk'
$staged = Join-Path $destination 'MemoMondes.apk.pending'
try {
    Copy-Item -LiteralPath $Source -Destination $staged -Force
    if ((Get-FileHash -LiteralPath $Source).Hash -ne (Get-FileHash -LiteralPath $staged).Hash) {
        throw 'La copie APK est invalide.'
    }
    Move-Item -LiteralPath $staged -Destination $final -Force
    # Remove only other APK files directly in the delivery folder.
    Get-ChildItem -LiteralPath $destination -File -Filter '*.apk' |
        Where-Object { $_.FullName -ne $final } |
        ForEach-Object { Remove-Item -LiteralPath $_.FullName -Force }
    Write-Output "APK final : $final"
} finally {
    if (Test-Path -LiteralPath $staged) { Remove-Item -LiteralPath $staged -Force }
}
