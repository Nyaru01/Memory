$ErrorActionPreference = 'Stop'
$root = 'D:\AndroidDev'
New-Item -ItemType Directory -Force "$root\tmp", "$root\android-emulator" | Out-Null
try {
    $result = Enable-WindowsOptionalFeature -Online -FeatureName HypervisorPlatform -All -NoRestart -ScratchDirectory "$root\tmp" -LogPath "$root\hypervisor-install.log"
    @{ Success = $true; RestartNeeded = [bool]$result.RestartNeeded; Feature = 'HypervisorPlatform' } |
        ConvertTo-Json | Set-Content "$root\hypervisor-status.json" -Encoding utf8
} catch {
    @{ Success = $false; Error = $_.Exception.Message } |
        ConvertTo-Json | Set-Content "$root\hypervisor-status.json" -Encoding utf8
    exit 1
}
