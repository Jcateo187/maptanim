Add-Type -AssemblyName System.Drawing
$source = "C:\Users\james cateo\.gemini\antigravity-ide\brain\6001f488-c5cc-422c-9be4-a1628ff4e402\.user_uploaded\media_1789534318924.jpg"
$dest = "d:\Development\MapTanim\mobile\app\src\main\res\drawable\loading_background.png"

$image = [System.Drawing.Image]::FromFile($source)
$image.Save($dest, [System.Drawing.Imaging.ImageFormat]::Png)
$image.Dispose()
Write-Host "PNG saved successfully to $dest"
