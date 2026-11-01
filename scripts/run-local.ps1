# Chạy server Spring Boot trên máy, chờ API trả 200 rồi cài và mở app trên mọi thiết bị đang cắm
# Cách chạy: powershell -ExecutionPolicy Bypass -File scripts\run-local.ps1 [-SkipCommon]
param([switch]$SkipCommon)

$ErrorActionPreference = 'Stop'
$root = Split-Path -Parent $PSScriptRoot
$url = 'http://localhost:8080/api/players/ranking'
$pkg = 'ptit.e23cnpm01.nguyenbahung.math_dash'

function Test-Server {
    try {
        return (Invoke-WebRequest -Uri $url -UseBasicParsing -TimeoutSec 3).StatusCode -eq 200
    } catch {
        return $false
    }
}

if (Test-Server) {
    Write-Host 'Server đã chạy sẵn'
} else {
    # Lớp thực thể dùng chung, bỏ qua bằng -SkipCommon khi không sửa common/
    if (-not $SkipCommon) {
        Write-Host 'Cài common vào kho Maven...'
        & mvn -q -f "$root\common\pom.xml" install -DskipTests
        if ($LASTEXITCODE -ne 0) { throw 'Cài common thất bại' }
    }
    Write-Host 'Mở server trong cửa sổ mới...'
    Start-Process powershell -WorkingDirectory "$root\server" -ArgumentList '-NoExit', '-Command', 'mvn spring-boot:run'
    $deadline = (Get-Date).AddSeconds(120)
    while (-not (Test-Server)) {
        if ((Get-Date) -gt $deadline) { throw 'Quá 120 giây server chưa trả 200, xem log ở cửa sổ server' }
        Start-Sleep -Seconds 2
    }
}
Write-Host 'Server trả 200 OK'

$devices = @(& adb devices | Select-Object -Skip 1 | Where-Object { $_ -match "`tdevice$" } | ForEach-Object { ($_ -split "`t")[0] })
if ($devices.Count -eq 0) { throw 'Không thấy thiết bị nào, kiểm tra cáp và gỡ lỗi USB' }
foreach ($d in $devices) {
    & adb -s $d reverse tcp:8080 tcp:8080 | Out-Null
    Write-Host "adb reverse 8080: $d"
}

# installDebug cài lên mọi thiết bị đang cắm
Push-Location $root
try {
    & .\gradlew.bat :app:installDebug
    if ($LASTEXITCODE -ne 0) { throw 'Cài app thất bại' }
} finally {
    Pop-Location
}

foreach ($d in $devices) {
    & adb -s $d shell monkey -p $pkg -c android.intent.category.LAUNCHER 1 | Out-Null
    Write-Host "Đã mở app trên $d"
}
