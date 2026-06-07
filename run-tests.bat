@echo off
REM ============================================================
REM  TaskFlow CLI - Compila y ejecuta la suite de tests propia
REM  (sin dependencias externas, solo el JDK).
REM
REM  Uso:  run-tests.bat
REM ============================================================

setlocal

REM Usar UTF-8 en la consola para que acentos y emojis se vean bien
chcp 65001 >nul

REM Ruta al JDK (ajusta si tu JDK esta en otra carpeta)
set "JDK=C:\Users\Quasir\.jdks\openjdk-26.0.1\bin"

if not exist "%JDK%\javac.exe" (
    echo [ERROR] No se encontro javac en "%JDK%".
    echo         Edita la variable JDK en este script con la ruta de tu JDK.
    exit /b 1
)

echo [1/3] Generando lista de archivos fuente...
if exist sources.txt del sources.txt
dir /b /s src\*.java > sources.txt
dir /b /s test\*.java >> sources.txt

echo [2/3] Compilando a la carpeta out...
"%JDK%\javac.exe" -d out @sources.txt
if errorlevel 1 (
    echo [ERROR] La compilacion fallo. Revisa los errores de arriba.
    exit /b 1
)

echo [3/3] Ejecutando tests...
echo.
"%JDK%\java.exe" -cp out com.taskflow.testkit.RunAllTests

endlocal
