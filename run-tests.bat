@echo off
REM ============================================================
REM  run-tests.bat  –  Compila y ejecuta los tests con JUnit 5
REM  Requiere: JDK 17+ en el PATH
REM ============================================================

setlocal

set SRC_DIR=src
set TEST_DIR=test
set OUT_DIR=out
set LIB_DIR=lib

set JUNIT_JAR=%LIB_DIR%\junit-platform-console-standalone-1.10.2.jar

REM ---- 1. Compilar fuentes principales ----
echo [1/3] Compilando fuentes principales...
if not exist %OUT_DIR% mkdir %OUT_DIR%

javac -d %OUT_DIR% -sourcepath %SRC_DIR% ^
  src\Main.java ^
  src\com\taskflow\model\Priority.java ^
  src\com\taskflow\model\Status.java ^
  src\com\taskflow\model\Task.java ^
  src\com\taskflow\common\Result.java ^
  src\com\taskflow\exception\InvalidTaskException.java ^
  src\com\taskflow\exception\PersistenceException.java ^
  src\com\taskflow\repository\Repository.java ^
  src\com\taskflow\repository\FileTaskRepository.java ^
  src\com\taskflow\service\TaskService.java ^
  src\com\taskflow\concurrent\BackgroundScheduler.java ^
  src\com\taskflow\cli\ConsoleApp.java

if errorlevel 1 (
    echo ERROR: fallo al compilar las fuentes principales.
    exit /b 1
)

REM ---- 2. Compilar tests ----
echo [2/3] Compilando tests...

javac -d %OUT_DIR% -cp "%OUT_DIR%;%JUNIT_JAR%" ^
  test\com\taskflow\testkit\TestRunner.java ^
  test\com\taskflow\testkit\RunAllTests.java ^
  test\com\taskflow\PriorityTest.java ^
  test\com\taskflow\StatusTest.java ^
  test\com\taskflow\TaskTest.java ^
  test\com\taskflow\TaskServiceTest.java ^
  test\com\taskflow\FileTaskRepositoryTest.java

if errorlevel 1 (
    echo ERROR: fallo al compilar los tests.
    exit /b 1
)

REM ---- 3. Ejecutar tests con JUnit Platform Console ----
echo [3/3] Ejecutando tests con JUnit 5...
echo.

java -jar %JUNIT_JAR% ^
  --class-path %OUT_DIR% ^
  --scan-class-path ^
  --details=tree

echo.
if errorlevel 1 (
    echo RESULTADO: hay tests en FALLO.
    exit /b 1
) else (
    echo RESULTADO: todos los tests pasaron.
    exit /b 0
)
