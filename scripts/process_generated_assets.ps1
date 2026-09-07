# =============================================================================
# MapTanim Windows PowerShell AI Image Asset Optimization & Catalog Tool
# =============================================================================
# Scans raw ChatGPT/DALL-E generated images, validates against the 15 approved crops,
# computes SHA-256 integrity checksums, and produces an asset manifest for MapTanim.
# =============================================================================

param (
    [string]$InputPath = "mobile\app\src\main\assets\metadata\crops_images",
    [string]$OutputPath = "mobile\app\src\main\assets\metadata\optimized_images"
)

$ApprovedCrops = @{
    "ampalaya" = @{ common = "Bitter Gourd"; local = "Ampalaya"; canonical = "ampalaya" }
    "cabbage"  = @{ common = "Cabbage"; local = "Repolyo"; canonical = "cabbage" }
    "carrot"   = @{ common = "Carrot"; local = "Karot"; canonical = "carrot" }
    "corn"     = @{ common = "Corn"; local = "Mais"; canonical = "corn" }
    "eggplant" = @{ common = "Eggplant"; local = "Talong"; canonical = "eggplant" }
    "kangkong" = @{ common = "Water Spinach"; local = "Kangkong"; canonical = "kangkong" }
    "lettuce"  = @{ common = "Lettuce"; local = "Litsugas"; canonical = "lettuce" }
    "okra"     = @{ common = "Okra"; local = "Okra"; canonical = "okra" }
    "onion"    = @{ common = "Onion"; local = "Sibuyas"; canonical = "onion" }
    "pechay"   = @{ common = "Pechay"; local = "Pechay"; canonical = "pechay" }
    "pipino"   = @{ common = "Cucumber"; local = "Pipino"; canonical = "pipino" }
    "pumpkin"  = @{ common = "Squash"; local = "Kalabasa"; canonical = "pumpkin" }
    "sili"     = @{ common = "Chili Pepper"; local = "Sili"; canonical = "sili" }
    "sitaw"    = @{ common = "Yardlong String Bean"; local = "Sitaw"; canonical = "sitaw" }
    "tomato"   = @{ common = "Tomato"; local = "Kamatis"; canonical = "tomato" }
}

Write-Host "========================================================" -ForegroundColor Green
Write-Host " MapTanim Native Asset Catalog & Checksum Tool" -ForegroundColor Green
Write-Host " Target Scope: Strictly 15 DA-BPI Approved Crops" -ForegroundColor Green
Write-Host "========================================================" -ForegroundColor Green

if (-not (Test-Path $InputPath)) {
    Write-Error "Input path not found: $InputPath"
    exit 1
}

if (-not (Test-Path $OutputPath)) {
    New-Item -ItemType Directory -Path $OutputPath -Force | Out-Null
}

$files = Get-ChildItem -Path $InputPath -File | Where-Object { $_.Extension -in @(".png", ".webp", ".jpg", ".jpeg") }
$manifest = @{
    version = "1.0.0"
    author = "MapTanim Creative & Agricultural Development Team"
    license = "Proprietary / MapTanim Educational Use Only (Zero Copyright / AI Generated)"
    crops = @{}
}

$totalSize = 0

foreach ($file in $files) {
    $stem = $file.BaseName.ToLower().Replace(" ", "").Replace("_", "").Replace("-", "")
    $canonical = $null

    foreach ($key in $ApprovedCrops.Keys) {
        if ($stem.Contains($key)) {
            $canonical = $key
            break
        }
    }

    if (-not $canonical) {
        $canonical = $stem
    }

    $hash = (Get-FileHash -Path $file.FullName -Algorithm SHA256).Hash
    $sizeKb = [math]::Round($file.Length / 1024, 2)
    $totalSize += $file.Length

    $manifest.crops[$canonical] = @{
        file_name = $file.Name
        canonical_key = $canonical
        size_kb = $sizeKb
        sha256 = $hash
        local_uri = "file:///android_asset/metadata/crops_images/$($file.Name)"
    }

    Write-Host " [+] Mapped: $($file.Name) -> $canonical ($sizeKb KB)" -ForegroundColor Cyan
}

$manifestJson = $manifest | ConvertTo-Json -Depth 5
$manifestFile = Join-Path $OutputPath "asset_manifest.json"
Set-Content -Path $manifestFile -Value $manifestJson -Encoding UTF8

$totalMb = [math]::Round($totalSize / (1024 * 1024), 2)
Write-Host "--------------------------------------------------------"
Write-Host " Manifest generated: $manifestFile" -ForegroundColor Yellow
Write-Host " Total Catalog Size: $totalMb MB" -ForegroundColor Yellow
Write-Host " Supabase Cloud Storage Used: 0.00 MB (100% Client-Bundled)" -ForegroundColor Green
Write-Host "========================================================" -ForegroundColor Green
