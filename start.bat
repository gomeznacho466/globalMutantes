@echo off
REM Script de inicio rápido para Mutant Detector
REM Ejecuta este archivo haciendo doble clic o desde PowerShell

echo ========================================
echo   MUTANT DETECTOR - API REST
echo   MercadoLibre Tech Challenge
echo ========================================
echo.

REM Verificar si existe el JAR
if not exist "target\mutant-detector-1.0.0.jar" (
    echo [ERROR] JAR no encontrado. Compilando proyecto...
    echo.
    call "C:\tools\apache-maven-3.9.11-bin\apache-maven-3.9.11\bin\mvn.cmd" clean install -DskipTests
    if errorlevel 1 (
        echo [ERROR] Fallo la compilacion.
        pause
        exit /b 1
    )
)

echo [INFO] Iniciando aplicacion...
echo [INFO] La API estara disponible en: http://localhost:8080
echo [INFO] Swagger UI: http://localhost:8080/swagger-ui.html
echo [INFO] H2 Console: http://localhost:8080/h2-console
echo.
echo Presiona Ctrl+C para detener la aplicacion
echo.

java -jar target\mutant-detector-1.0.0.jar

pause
