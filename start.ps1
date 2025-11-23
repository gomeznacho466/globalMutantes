# Script PowerShell para iniciar Mutant Detector
# Uso: .\start.ps1

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  MUTANT DETECTOR - API REST" -ForegroundColor Cyan
Write-Host "  MercadoLibre Tech Challenge" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Verificar si existe el JAR
if (!(Test-Path "target\mutant-detector-1.0.0.jar")) {
    Write-Host "[ERROR] JAR no encontrado. Compilando proyecto..." -ForegroundColor Yellow
    Write-Host ""
    
    & "C:\tools\apache-maven-3.9.11-bin\apache-maven-3.9.11\bin\mvn.cmd" clean install -DskipTests
    
    if ($LASTEXITCODE -ne 0) {
        Write-Host "[ERROR] Falló la compilación." -ForegroundColor Red
        Read-Host "Presiona Enter para salir"
        exit 1
    }
}

Write-Host "[INFO] Iniciando aplicación..." -ForegroundColor Green
Write-Host "[INFO] La API estará disponible en: http://localhost:8080" -ForegroundColor Green
Write-Host "[INFO] Swagger UI: http://localhost:8080/swagger-ui.html" -ForegroundColor Green
Write-Host "[INFO] H2 Console: http://localhost:8080/h2-console" -ForegroundColor Green
Write-Host ""
Write-Host "Presiona Ctrl+C para detener la aplicación" -ForegroundColor Yellow
Write-Host ""

java -jar target\mutant-detector-1.0.0.jar
