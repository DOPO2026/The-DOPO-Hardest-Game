param([switch]$Run)

$jdkBin  = "C:\Users\nart6\.jdks\openjdk-26.0.1\bin"
$root    = $PSScriptRoot
$classes = "$root\out\production\The-DOPO-Hardest-Game"
$artDir  = "$root\out\artifacts\TheDOPOHardestGame"
$jarFile = "$artDir\TheDOPOHardestGame.jar"

Write-Host "=== Compilando fuentes ===" -ForegroundColor Cyan
Remove-Item -Recurse -Force $classes -ErrorAction SilentlyContinue
New-Item -ItemType Directory -Force $classes | Out-Null

$sources = Get-ChildItem -Recurse "$root\src" -Filter "*.java" |
    Where-Object { $_.FullName -notlike "*\src\test\*" } |
    ForEach-Object { $_.FullName }

& "$jdkBin\javac.exe" -d $classes @sources
if ($LASTEXITCODE -ne 0) { Write-Host "ERROR de compilacion" -ForegroundColor Red; exit 1 }
Write-Host "OK - $($sources.Count) archivos compilados" -ForegroundColor Green

Write-Host "=== Empaquetando JAR ===" -ForegroundColor Cyan
New-Item -ItemType Directory -Force $artDir | Out-Null
$manifest = "$artDir\MANIFEST.MF"
[System.IO.File]::WriteAllText($manifest, "Main-Class: presentation.TheDOPOHardestGameGUI`n", [System.Text.Encoding]::ASCII)

Push-Location $classes
& "$jdkBin\jar.exe" --create --file $jarFile --manifest $manifest .
Pop-Location

Push-Location $root
& "$jdkBin\jar.exe" --update --file $jarFile resources
Pop-Location

$kb = [math]::Round((Get-Item $jarFile).Length / 1KB, 1)
Write-Host "OK - $jarFile ($kb KB)" -ForegroundColor Green

if ($Run) {
    Write-Host "=== Ejecutando ===" -ForegroundColor Cyan
    & "$jdkBin\java.exe" -jar $jarFile
}
