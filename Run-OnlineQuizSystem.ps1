# ====================================================================
# Run-OnlineQuizSystem.ps1
# Script to build and run the Online Quiz System on embedded Apache Tomcat
# ====================================================================

$projectDir = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $projectDir

Write-Host ">>> Compiling Java sources..." -ForegroundColor Cyan
$sourceFiles = Get-ChildItem -Path "$projectDir\Java" -Recurse -Filter "*.java" | Select-Object -ExpandProperty FullName
$outDir = "$projectDir\build\classes"
if (!(Test-Path $outDir)) { New-Item -ItemType Directory -Path $outDir -Force | Out-Null }

$libDir = "$projectDir\lib"
$jars = Get-ChildItem -Path $libDir -Filter "*.jar" | Select-Object -ExpandProperty FullName
$cp = "$outDir;" + ($jars -join ";")

javac -cp $cp -d $outDir $sourceFiles
if ($LASTEXITCODE -ne 0) {
    Write-Host "Compilation failed with exit code $LASTEXITCODE" -ForegroundColor Red
    exit $LASTEXITCODE
}
Write-Host ">>> Compilation successful." -ForegroundColor Green

# Ensure classes and jars are copied to web/WEB-INF for Tomcat
$webClasses = "$projectDir\web\WEB-INF\classes"
if (!(Test-Path $webClasses)) { New-Item -ItemType Directory -Path $webClasses -Force | Out-Null }
Copy-Item -Path "$outDir\*" -Destination $webClasses -Recurse -Force

$webLib = "$projectDir\web\WEB-INF\lib"
if (!(Test-Path $webLib)) { New-Item -ItemType Directory -Path $webLib -Force | Out-Null }
Copy-Item "$libDir\h2.jar", "$libDir\ojdbc8.jar" -Destination $webLib -Force

Write-Host ">>> Starting Online Quiz System server on http://localhost:8080..." -ForegroundColor Cyan
Write-Host ">>> Press Ctrl+C in this terminal to stop the server." -ForegroundColor Yellow
java -cp $cp runner.EmbeddedTomcatRunner 8080
