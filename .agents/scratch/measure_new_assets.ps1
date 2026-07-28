Add-Type -AssemblyName System.Drawing
$folders = @("fences", "tiles", "trees_and_rocks")
foreach ($folder in $folders) {
    $dir = "d:\Development\MapTanim\mobile\app\src\main\assets\$folder"
    $files = Get-ChildItem -Path $dir -Filter "*.png"
    foreach ($f in $files) {
        $img = [System.Drawing.Image]::FromFile($f.FullName)
        Write-Output "$folder/$($f.Name) : $($img.Width) x $($img.Height)"
        $img.Dispose()
    }
}
