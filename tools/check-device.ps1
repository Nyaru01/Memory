param([string]$Serial = 'emulator-5554', [string]$ToolRoot = 'D:\AndroidDev', [switch]$SkipBuild)
$ErrorActionPreference = 'Stop'
if ([IO.Path]::GetPathRoot([IO.Path]::GetFullPath($ToolRoot)) -ne 'D:\') { throw 'Les outils doivent rester sur D:.' }
if ($Serial -notmatch '^emulator-\d+$') { throw 'Ces scénarios modifient la progression ; utiliser un émulateur dédié.' }
$adb = Join-Path $ToolRoot 'sdk\platform-tools\adb.exe'
$project = Split-Path $PSScriptRoot -Parent
$output = Join-Path $project 'releases\validation'
New-Item -ItemType Directory -Force $output | Out-Null
$previousAndroid = $env:ANDROID_USER_HOME
$previousEmulator = $env:ANDROID_EMULATOR_HOME
try {
    $env:ANDROID_USER_HOME = Join-Path $ToolRoot 'android-user'
    $env:ANDROID_EMULATOR_HOME = Join-Path $ToolRoot 'android-emulator'
    if (!$SkipBuild) { & "$PSScriptRoot\build.ps1" -ToolRoot $ToolRoot -Tasks ':app:assembleDebug', ':app:assembleDebugAndroidTest' }
    & $adb -s $Serial install -r "$project\app\build\outputs\apk\debug\app-debug.apk"
    if ($LASTEXITCODE -ne 0) { throw 'Installation application impossible.' }
    & $adb -s $Serial install -r "$project\app\build\outputs\apk\androidTest\debug\app-debug-androidTest.apk"
    if ($LASTEXITCODE -ne 0) { throw 'Installation des vérifications impossible.' }
    $result = & $adb -s $Serial shell am instrument -w 'fr.memomimi.app.test/fr.memomimi.app.VisualEffectsChecks'
    $result | Set-Content (Join-Path $output 'device-checks.txt') -Encoding utf8
    $result | Write-Output
    if ($LASTEXITCODE -ne 0 -or ($result -join "`n") -notmatch 'PASS:') { throw 'Vérifications Android en échec ; consulter releases/validation/device-checks.txt.' }
    & $adb -s $Serial pull '/sdcard/Android/data/fr.memomimi.app/files/.' $output
    if ($LASTEXITCODE -ne 0) { throw 'Récupération des captures impossible.' }
} finally {
    $env:ANDROID_USER_HOME = $previousAndroid
    $env:ANDROID_EMULATOR_HOME = $previousEmulator
}
