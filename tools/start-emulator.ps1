param([string]$ToolRoot = 'D:\AndroidDev', [switch]$Headless)
$ErrorActionPreference = 'Stop'
if ([IO.Path]::GetPathRoot([IO.Path]::GetFullPath($ToolRoot)) -ne 'D:\') { throw 'Les outils doivent rester sur D:.' }
$emulator = Join-Path $ToolRoot 'sdk\emulator\emulator.exe'
$adb = Join-Path $ToolRoot 'sdk\platform-tools\adb.exe'
$previous = @{}
$variables = @('ANDROID_HOME', 'ANDROID_USER_HOME', 'ANDROID_EMULATOR_HOME', 'ANDROID_AVD_HOME', 'TEMP', 'TMP', 'QT_OPENGL')
foreach ($name in $variables) { $previous[$name] = [Environment]::GetEnvironmentVariable($name, 'Process') }
try {
    $env:ANDROID_HOME = Join-Path $ToolRoot 'sdk'
    $env:ANDROID_USER_HOME = Join-Path $ToolRoot 'android-user'
    $env:ANDROID_EMULATOR_HOME = Join-Path $ToolRoot 'android-emulator'
    $env:ANDROID_AVD_HOME = Join-Path $ToolRoot 'avd'
    $env:TEMP = Join-Path $ToolRoot 'tmp'
    $env:TMP = $env:TEMP
    New-Item -ItemType Directory -Force $env:ANDROID_EMULATOR_HOME, $env:TEMP | Out-Null
    & $emulator -accel-check
    if ($LASTEXITCODE -ne 0) { throw 'Accélération Windows indisponible. Activer HypervisorPlatform puis redémarrer Windows.' }
    $env:QT_OPENGL = 'desktop'
    $arguments = '-avd MemoMondes35 -port 5554 -no-snapshot -no-boot-anim -no-metrics -gpu host -feature -Vulkan -memory 2048'
    if ($Headless) { $arguments += ' -no-window -no-audio' }
    $running = Get-CimInstance Win32_Process | Where-Object {
        $_.ExecutablePath -like "$ToolRoot\sdk\emulator\*" -and $_.CommandLine -match '-avd MemoMondes35( |$)'
    }
    if (!$running) {
        Start-Process -FilePath $emulator -ArgumentList $arguments -WindowStyle $(if ($Headless) { 'Hidden' } else { 'Normal' }) -RedirectStandardOutput (Join-Path $ToolRoot 'emulator-start.log') -RedirectStandardError (Join-Path $ToolRoot 'emulator-start-error.log')
    }
    $installer = Join-Path $PSScriptRoot 'install-on-emulator.ps1'
    Start-Process -FilePath (Get-Process -Id $PID).Path -ArgumentList "-NoProfile -ExecutionPolicy Bypass -File `"$installer`" -ToolRoot `"$ToolRoot`"" -WindowStyle Hidden -RedirectStandardOutput (Join-Path $ToolRoot 'emulator-app-install.log') -RedirectStandardError (Join-Path $ToolRoot 'emulator-app-install-error.log')
    Write-Output 'Émulateur MemoMondes35 lancé ; attendre son démarrage Android.'
} finally {
    foreach ($name in $variables) { [Environment]::SetEnvironmentVariable($name, $previous[$name], 'Process') }
}
