$ErrorActionPreference = "Stop"
$projectDir = $PSScriptRoot
$libJar     = "$projectDir\lib\junit-platform-console-standalone-1.10.2.jar"
$outDir     = "$projectDir\out\test"
$sep        = "-" * 60

# Buscar javac: primero en PATH, luego en ubicaciones conocidas
$javacCmd = Get-Command javac -ErrorAction SilentlyContinue
$javaCmd  = Get-Command java  -ErrorAction SilentlyContinue
$javac = if ($javacCmd) { $javacCmd.Source } else { $null }
$java  = if ($javaCmd)  { $javaCmd.Source  } else { $null }

if (-not $javac) {
    $candidates = @(
        "$env:USERPROFILE\.jdks\openjdk-26.0.1\bin\javac.exe",
        "$env:JAVA_HOME\bin\javac.exe",
        "C:\Program Files\Java\jdk-21\bin\javac.exe"
    )
    $javac = $candidates | Where-Object { Test-Path $_ } | Select-Object -First 1
    $java  = $javac -replace "javac.exe","java.exe"
}

if (-not $javac) {
    Write-Host "[ERROR] No se encontro javac. Agrega el JDK al PATH." -ForegroundColor Red
    Write-Host "Tip: Anade C:\Users\$env:USERNAME\.jdks\openjdk-26.0.1\bin al PATH."
    exit 1
}

Write-Host "JDK: $javac" -ForegroundColor DarkGray

# 1. Limpiar y crear directorio de salida
if (Test-Path $outDir) { Remove-Item -Recurse -Force $outDir }
New-Item -ItemType Directory -Force -Path $outDir | Out-Null

# 2. Reunir todos los .java de domain y test
$javaFiles = Get-ChildItem -Recurse -Filter "*.java" `
    -Path "$projectDir\src\domain","$projectDir\src\test" |
    Select-Object -ExpandProperty FullName

if (-not $javaFiles) {
    Write-Host "[ERROR] No se encontraron archivos .java" -ForegroundColor Red
    exit 1
}

# Escribir lista en fichero temporal (evita limite de longitud en cmd)
$fileList = [System.IO.Path]::GetTempFileName()
[System.IO.File]::WriteAllLines($fileList, $javaFiles)

# 3. Compilar
Write-Host ""
Write-Host "Compilando $($javaFiles.Count) archivos..." -ForegroundColor Cyan
& $javac -encoding UTF-8 -cp $libJar -d $outDir "@$fileList"
Remove-Item $fileList

if ($LASTEXITCODE -ne 0) {
    Write-Host "[ERROR] Compilacion fallida." -ForegroundColor Red
    exit 1
}
Write-Host "Compilacion exitosa." -ForegroundColor Green

# 4. Ejecutar tests
Write-Host ""
Write-Host "Ejecutando tests..." -ForegroundColor Cyan
Write-Host $sep
& $java -jar $libJar --class-path $outDir --scan-class-path --details=tree
$exitCode = $LASTEXITCODE
Write-Host $sep

if ($exitCode -eq 0) {
    Write-Host "Todos los tests pasaron." -ForegroundColor Green
} else {
    Write-Host "Algunos tests fallaron (exit code $exitCode)." -ForegroundColor Red
}
exit $exitCode
