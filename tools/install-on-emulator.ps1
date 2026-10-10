param([string]$ToolRoot = 'D:\AndroidDev', [string]$Serial = 'emulator-5554')
$ErrorActionPreference = 'Stop'
if ([IO.Path]::GetPathRoot([IO.Path]::GetFullPath($ToolRoot)) -ne 'D:\') { throw 'Les outils doivent rester sur D:.' }
if ($Serial -notmatch '^emulator-\d+$') { throw 'Utiliser un émulateur dédié.' }
$env:ANDROID_USER_HOME = Join-Path $ToolRoot 'android-user'
$env:ANDROID_EMULATOR_HOME = Join-Path $ToolRoot 'android-emulator'
$env:TEMP = Join-Path $ToolRoot 'tmp'
$env:TMP = $env:TEMP
$adb = Join-Path $ToolRoot 'sdk\platform-tools\adb.exe'
# Windows PowerShell 5 traite les messages stderr d'ADB comme des erreurs.
# Les codes de sortie restent la référence pour les commandes natives.
$ErrorActionPreference = 'Continue'
& $adb start-server
if ($LASTEXITCODE -ne 0) { throw 'Démarrage ADB impossible.' }
$deadline = [DateTime]::UtcNow.AddMinutes(4)
do {
    $booted = & $adb -s $Serial shell getprop sys.boot_completed 2>$null
    if (($booted -join '').Trim() -eq '1') { break }
    Start-Sleep -Seconds 2
} while ([DateTime]::UtcNow -lt $deadline)
if (($booted -join '').Trim() -ne '1') { throw 'Le démarrage Android a dépassé quatre minutes.' }
$ErrorActionPreference = 'Stop'
$apk = Join-Path (Split-Path $PSScriptRoot -Parent) 'APK\MemoMondes.apk'
& $adb -s $Serial install -r $apk
if ($LASTEXITCODE -ne 0) { throw 'Installation APK impossible.' }
& $adb -s $Serial shell am start -n 'fr.memomimi.app/.MainActivity'
if ($LASTEXITCODE -ne 0) { throw 'Lancement application impossible.' }
