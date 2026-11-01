# Đánh thức server trên Render, chờ API trả 200 rồi cài và mở app trỏ tới Render trên mọi thiết bị đang cắm
# Cách chạy: powershell -ExecutionPolicy Bypass -File scripts\run-render.ps1 [-ServerUrl https://...onrender.com]
# ApiDAO.java chỉ bị đổi BASE_URL trong lúc build, xong (kể cả lỗi hay Ctrl+C) sẽ trả lại nguyên bản
param([string]$ServerUrl = 'https://math-dash-server.onrender.com')

$ErrorActionPreference = 'Stop'
$root = Split-Path -Parent $PSScriptRoot
$apiUrl = "$ServerUrl/api"
$url = "$apiUrl/players/ranking"
$pkg = 'ptit.e23cnpm01.nguyenbahung.math_dash'
$apiDao = "$root\app\src\main\java\ptit\e23cnpm01\nguyenbahung\math_dash\dao\ApiDAO.java"
$backup = "$env:TEMP\math-dash-ApiDAO.java.bak"
$localUrl = '"http://localhost:8080/api"'

function Test-Server {
    try {
        return (Invoke-WebRequest -Uri $url -UseBasicParsing -TimeoutSec 30).StatusCode -eq 200
    } catch {
        return $false
    }
}

# Lần trước bị tắt ngang giữa lúc build thì trả file về trước
if (Test-Path $backup) {
    Copy-Item $backup $apiDao -Force
    Remove-Item $backup
    Write-Host 'Đã khôi phục ApiDAO.java từ lần chạy trước'
}

# Gói free của Render ngủ sau 15 phút, lần gọi đầu mất khoảng 1 phút để dậy
Write-Host "Gọi $url ..."
$deadline = (Get-Date).AddSeconds(180)
while (-not (Test-Server)) {
    if ((Get-Date) -gt $deadline) { throw 'Quá 180 giây Render chưa trả 200, xem log trên dashboard Render' }
    Write-Host 'Server chưa sẵn sàng, thử lại...'
    Start-Sleep -Seconds 5
}
Write-Host 'Server Render trả 200 OK'

$devices = @(& adb devices | Select-Object -Skip 1 | Where-Object { $_ -match "`tdevice$" } | ForEach-Object { ($_ -split "`t")[0] })
if ($devices.Count -eq 0) { throw 'Không thấy thiết bị nào, kiểm tra cáp và gỡ lỗi USB' }
foreach ($d in $devices) {
    # Gỡ adb reverse của bản local cho chắc app không gọi nhầm server trên máy
    # Chỉ gỡ khi có, vì adb báo lỗi ra stderr và PowerShell 5.1 coi đó là lỗi dừng script
    if (& adb -s $d reverse --list | Select-String 'tcp:8080') {
        & adb -s $d reverse --remove tcp:8080 | Out-Null
        Write-Host "Gỡ adb reverse 8080: $d"
    }
}

$source = [IO.File]::ReadAllText($apiDao)
if (-not $source.Contains($localUrl)) { throw "Không thấy $localUrl trong ApiDAO.java, BASE_URL đã đổi khác" }

Copy-Item $apiDao $backup -Force
Push-Location $root
try {
    [IO.File]::WriteAllText($apiDao, $source.Replace($localUrl, "`"$apiUrl`""), (New-Object Text.UTF8Encoding $false))
    Write-Host "Build app với BASE_URL = $apiUrl"
    # installDebug cài lên mọi thiết bị đang cắm
    & .\gradlew.bat :app:installDebug
    if ($LASTEXITCODE -ne 0) { throw 'Cài app thất bại' }
} finally {
    Pop-Location
    Copy-Item $backup $apiDao -Force
    Remove-Item $backup
    Write-Host 'Đã trả ApiDAO.java về nguyên bản'
}

foreach ($d in $devices) {
    & adb -s $d shell monkey -p $pkg -c android.intent.category.LAUNCHER 1 | Out-Null
    Write-Host "Đã mở app trên $d"
}
Write-Host 'App gọi thẳng Render qua internet, rút cáp vẫn chơi được'
