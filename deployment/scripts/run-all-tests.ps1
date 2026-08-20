# MapTanim - Unified Multi-Platform Test Runner (PowerShell)

Write-Host "=========================================================" -ForegroundColor Green
Write-Host "  🧪 MapTanim Unified Test Suite Execution" -ForegroundColor Green
Write-Host "=========================================================" -ForegroundColor Green
Write-Host ""

$RootDir = Resolve-Path "$PSScriptRoot\..\.."
Set-Location $RootDir

$failed = $false

# Step 1: Admin Dashboard E2E & TypeScript Compilation
Write-Host "[1/5] Testing Admin Web Dashboard (TypeScript & E2E)..." -ForegroundColor Cyan
Set-Location "$RootDir\admin"
try {
    npx tsc --noEmit
    if ($LASTEXITCODE -eq 0) {
        Write-Host "  ✅ TypeScript check passed!" -ForegroundColor Green
    } else {
        Write-Host "  ❌ TypeScript check failed!" -ForegroundColor Red
        $failed = $true
    }
} catch {
    Write-Host "  ❌ Admin test error: $_" -ForegroundColor Red
    $failed = $true
}

Set-Location $RootDir
try {
    node "$RootDir\tests\ui\admin_e2e_test.mjs"
} catch {
    Write-Host "  ❌ Admin E2E test failed!" -ForegroundColor Red
    $failed = $true
}

# Step 2: Integration & Database Tests
Write-Host "`n[2/5] Running API & Database Integration Tests..." -ForegroundColor Cyan
try {
    py "$RootDir\tests\integration\api_integration_test.py"
} catch {
    Write-Host "  ❌ Integration test failed!" -ForegroundColor Red
    $failed = $true
}

# Step 3: Unit & DSS Engine Tests
Write-Host "`n[3/5] Running Mobile & DSS Engine Unit Tests..." -ForegroundColor Cyan
try {
    py "$RootDir\tests\unit\dss_matrix_test.py"
} catch {
    Write-Host "  ❌ Mobile DSS test failed!" -ForegroundColor Red
    $failed = $true
}

# Step 4: Performance & Load Benchmark Tests
Write-Host "`n[4/5] Running Performance Load Simulation Tests..." -ForegroundColor Cyan
try {
    py "$RootDir\tests\performance\load_simulation_test.py"
} catch {
    Write-Host "  ❌ Performance test failed!" -ForegroundColor Red
    $failed = $true
}

# Step 5: Mobile & Backend Gradle Unit Tests
Write-Host "`n[5/5] Running Mobile & Backend Gradle Unit Tests..." -ForegroundColor Cyan
try {
    .\gradlew.bat test
    if ($LASTEXITCODE -eq 0) {
        Write-Host "  ✅ Gradle unit tests passed!" -ForegroundColor Green
    } else {
        Write-Host "  ❌ Gradle unit tests failed!" -ForegroundColor Red
        $failed = $true
    }
} catch {
    Write-Host "  ❌ Gradle execution failed: $_" -ForegroundColor Red
    $failed = $true
}

Write-Host "`n=========================================================" -ForegroundColor Green
if ($failed) {
    Write-Host "  ❌ Some tests failed. Please review the output logs." -ForegroundColor Red
    exit 1
} else {
    Write-Host "  🎉 All test suites passed successfully!" -ForegroundColor Green
    exit 0
}
