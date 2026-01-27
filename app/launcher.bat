@echo off
REM ═══════════════════════════════════════════════════════════════════
REM  Script de lanzamiento para AutoReach
REM  Ejecuta la aplicacion JavaFX con los modulos necesarios
REM ═══════════════════════════════════════════════════════════════════

setlocal

REM Obtener directorio del script
set "APP_HOME=%~dp0"
cd /d "%APP_HOME%"

REM Nombre del JAR principal
set "JAR_NAME=app.jar"

REM Configuracion de memoria (ajustar segun necesidad)
set "JAVA_OPTS=-Xms256m -Xmx1024m"

REM Modulos de JavaFX necesarios
set "JAVAFX_MODULES=javafx.controls,javafx.graphics,javafx.fxml,javafx.web,javafx.swing,javafx.media"

REM Verificar que existe el JAR
if not exist "%JAR_NAME%" (
    echo ERROR: No se encontro %JAR_NAME%
    echo Asegurate de estar en el directorio correcto.
    pause
    exit /b 1
)

REM Verificar que existe la carpeta lib
if not exist "lib" (
    echo ERROR: No se encontro la carpeta lib/
    echo Asegurate de que las dependencias estan copiadas.
    pause
    exit /b 1
)

REM Ejecutar la aplicacion
echo Iniciando AutoReach...
java %JAVA_OPTS% ^
    --module-path "lib" ^
    --add-modules %JAVAFX_MODULES% ^
    --add-opens javafx.graphics/javafx.scene=org.controlsfx.controls ^
    --add-opens javafx.graphics/javafx.scene=ALL-UNNAMED ^
    -jar "%JAR_NAME%"

REM Capturar codigo de salida
set "EXIT_CODE=%ERRORLEVEL%"

if %EXIT_CODE% neq 0 (
    echo.
    echo La aplicacion termino con codigo de error: %EXIT_CODE%
    pause
)

endlocal
exit /b %EXIT_CODE%
