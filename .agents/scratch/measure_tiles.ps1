Add-Type -AssemblyName System.Drawing
$dir = "d:\Development\MapTanim\mobile\app\src\main\res\drawable"
$files = @("grass_01.png","grass_02.png","grass_03.png","grass_04.png","grass_05.png","soil_01.png","soil_02.png","soil_03.png")
foreach ($f in $files) {
    $path = Join-Path $dir $f
    if (Test-Path $path) {
        $img = [System.Drawing.Image]::FromFile($path)
        Write-Output "$f : $($img.Width) x $($img.Height)"
        $img.Dispose()
    } else {
        Write-Output "$f : NOT FOUND"
    }
}
