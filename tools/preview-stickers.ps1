Add-Type -AssemblyName System.Drawing
$ErrorActionPreference = 'Stop'

function Test-PaleBackground([int]$pixel) {
    $red = ($pixel -shr 16) -band 255
    $green = ($pixel -shr 8) -band 255
    $blue = $pixel -band 255
    $highest = [Math]::Max($red, [Math]::Max($green, $blue))
    $lowest = [Math]::Min($red, [Math]::Min($green, $blue))
    return $red -ge 222 -and $green -ge 210 -and $blue -ge 186 -and ($highest - $lowest) -le 65
}

function Add-BackgroundPixel([int]$position, [int[]]$pixels, [bool[]]$background, [int[]]$queue, [int]$tail) {
    if (-not $background[$position] -and (Test-PaleBackground $pixels[$position])) {
        $background[$position] = $true
        $queue[$tail] = $position
        return ($tail + 1)
    }
    return $tail
}

$assetRoot = Join-Path $PSScriptRoot '..\app\src\main\assets'
$outputRoot = Join-Path $PSScriptRoot '..\build\icon-previews'
New-Item -ItemType Directory -Path $outputRoot -Force | Out-Null

foreach ($item in @(
    @('animals.png',0), @('ocean.png',1), @('space.png',2),
    @('dinosaurs.png',3), @('farm.png',4), @('vehicles.png',5),
    @('magic.png',6), @('mermaids.png',7), @('jungle.png',8),
    @('treats.png',9), @('pirates.png',10), @('arctic.png',11),
    @('music_world.png',12), @('jobs.png',13), @('fairies.png',14),
    @('home_world.png',15), @('vegetables.png',16), @('detective.png',17))) {
    $name = $item[0]
    $world = [int]$item[1]
    $source = [System.Drawing.Bitmap]::new((Join-Path $assetRoot $name))
    $width = [int]($source.Width / 4)
    $height = [int]($source.Height / 3)
    $size = $width * $height
    $pixels = [int[]]::new($size)
    for ($y=0; $y -lt $height; $y++) {
        for ($x=0; $x -lt $width; $x++) {
            $pixels[$y*$width+$x] = $source.GetPixel($x,$y).ToArgb()
        }
    }
    $background = [bool[]]::new($size)
    $keyBackground = $world -notin @(13)
    if ($keyBackground) {
        $queue = [int[]]::new($size)
        $tail = 0
        for ($x=0; $x -lt $width; $x++) {
            $tail = Add-BackgroundPixel $x $pixels $background $queue $tail
            $tail = Add-BackgroundPixel (($height-1)*$width+$x) $pixels $background $queue $tail
        }
        for ($y=0; $y -lt $height; $y++) {
            $tail = Add-BackgroundPixel ($y*$width) $pixels $background $queue $tail
            $tail = Add-BackgroundPixel ($y*$width+$width-1) $pixels $background $queue $tail
        }
        for ($head=0; $head -lt $tail; $head++) {
            $position = $queue[$head]
            $x = $position % $width
            if ($x -gt 0) { $tail = Add-BackgroundPixel ($position-1) $pixels $background $queue $tail }
            if ($x+1 -lt $width) { $tail = Add-BackgroundPixel ($position+1) $pixels $background $queue $tail }
            if ($position -ge $width) { $tail = Add-BackgroundPixel ($position-$width) $pixels $background $queue $tail }
            if ($position+$width -lt $size) { $tail = Add-BackgroundPixel ($position+$width) $pixels $background $queue $tail }
        }
    }
    $feather = if ($keyBackground) { 0.10 } else { 0.18 }
    $preview = [System.Drawing.Bitmap]::new($width, $height)
    for ($y=0; $y -lt $height; $y++) {
        $edgeY = [Math]::Min(1.0, [Math]::Min($y,$height-1-$y)/($height*$feather))
        for ($x=0; $x -lt $width; $x++) {
            $position = $y*$width+$x
            if ($background[$position]) { continue }
            $edgeX = [Math]::Min(1.0, [Math]::Min($x,$width-1-$x)/($width*$feather))
            $edge = [Math]::Min($edgeX,$edgeY)
            $edge = $edge*$edge*(3-2*$edge)
            $argb = ([long]$pixels[$position]) -band 4294967295
            $alpha = [int][Math]::Round((($argb -shr 24) -band 255)*$edge)
            $color = [System.Drawing.Color]::FromArgb($alpha,($argb -shr 16) -band 255,($argb -shr 8) -band 255,$argb -band 255)
            $preview.SetPixel($x,$y,$color)
        }
    }
    $preview.Save((Join-Path $outputRoot $name), [System.Drawing.Imaging.ImageFormat]::Png)
    $preview.Dispose()
    $source.Dispose()
}
