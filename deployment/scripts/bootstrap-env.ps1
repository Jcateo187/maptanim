# MapTanim - Local Developer Environment Setup Script (PowerShell)

Write-Host "=========================================================" -ForegroundColor Green
Write-Host "  🌱 MapTanim Developer Environment Setup & Verification" -ForegroundColor Green
Write-Host "=========================================================" -ForegroundColor Green
Write-Host ""

$RootDir = Resolve-Path "$PSScriptRoot\..\.."
Set-Location $RootDir

# 1. Verify Prerequisites
Write-Host "[1/4] Checking Prerequisites..." -ForegroundColor Cyan

# Check Java / JDK
try {
    $javaVer = java -version 2>&1 | Out-String
    Write-Host "  ✅ Java detected." -ForegroundColor Green
} catch {
    Write-Host "  ❌ Java (JDK 17) is missing! Please install JDK 17." -ForegroundColor Red
}

# Check Node.js
try {
    $nodeVer = node --version
    Write-Host "  ✅ Node.js detected: $nodeVer" -ForegroundColor Green
} catch {
    Write-Host "  ❌ Node.js is missing! Please install Node 20+." -ForegroundColor Red
}

# Check Docker
try {
    $dockerVer = docker --version
    Write-Host "  ✅ Docker detected: $dockerVer" -ForegroundColor Green
} catch {
    Write-Host "  ⚠️ Docker not detected. Docker container features will be unavailable." -ForegroundColor Yellow
}

# 2. Setup Environment Variables File
Write-Host "`n[2/4] Setting up Environment Variables..." -ForegroundColor Cyan
if (-not (Test-Path "$RootDir\deployment\.env")) {
    Copy-Item "$RootDir\deployment\.env.example" "$RootDir\deployment\.env"
    Write-Host "  ✅ Created deployment\.env from .env.example" -ForegroundColor Green
} else {
    Write-Host "  ℹ️ deployment\.env already exists." -ForegroundColor Yellow
}

# 3. Install Admin Dependencies
Write-Host "`n[3/4] Installing Admin Web Dashboard Dependencies..." -ForegroundColor Cyan
if (Test-Path "$RootDir\admin\package.json") {
    Set-Location "$RootDir\admin"
    npm install
    Write-Host "  ✅ Admin dependencies installed." -ForegroundColor Green
    Set-Location $RootDir
}

# 4. Verify Gradle Wrapper
Write-Host "`n[4/4] Verifying Gradle Setup..." -ForegroundColor Cyan
if (Test-Path "$RootDir\gradlew.bat") {
    Write-Host "  ✅ Gradle wrapper present." -ForegroundColor Green
} else {
    Write-Host "  ❌ gradlew.bat missing!" -ForegroundColor Red
}

Write-Host "`n=========================================================" -ForegroundColor Green
Write-Host "  🎉 Environment setup check completed successfully!" -ForegroundColor Green
Write-Host "=========================================================" -ForegroundColor Green
