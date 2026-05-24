@echo off
setlocal EnableExtensions EnableDelayedExpansion
chcp 65001 >nul
echo ========================================
echo  Vehicle Rental System - Start Script
echo ========================================
echo.

:: Navigate to project directory
cd /d "%~dp0"

set "PORT=%~1"
if not defined PORT set "PORT=8081"

set "MVN="
if exist "%~dp0apache-maven-3.9.6\bin\mvn.cmd" set "MVN=%~dp0apache-maven-3.9.6\bin\mvn.cmd"
if not defined MVN (
    for /f "delims=" %%I in ('where mvn.cmd 2^>nul') do if not defined MVN set "MVN=%%~fI"
)
if not defined MVN (
    for /f "delims=" %%I in ('where mvn 2^>nul') do if not defined MVN set "MVN=%%~fI"
)
if not defined MVN (
    echo ERROR: Maven was not found.
    echo        Expected bundled Maven at: %~dp0apache-maven-3.9.6\bin\mvn.cmd
    echo        Or Maven on PATH as mvn.cmd / mvn.
    pause
    exit /b 1
)

:: Terminate any process running on the selected port
echo [1/4] Checking for processes on port %PORT%...
powershell -NoProfile -ExecutionPolicy Bypass -Command "$port=%PORT%; $pids = @(Get-NetTCPConnection -LocalPort $port -State Listen -ErrorAction SilentlyContinue ^| Select-Object -ExpandProperty OwningProcess -Unique); foreach ($pid in $pids) { Write-Host ('    Found process PID: ' + $pid + ' - Terminating...'); Stop-Process -Id $pid -Force -ErrorAction SilentlyContinue }"
echo     Port %PORT% cleared.
echo.

:: Wait for port to be released
timeout /t 2 /nobreak >nul

:: Clean up previous builds
echo [2/4] Cleaning previous builds...
if exist target rmdir /s /q target
echo     Clean complete.
echo.

:: Build the project
echo [3/4] Building project...
call "%MVN%" clean compile -DskipTests
if errorlevel 1 (
    echo.
    echo     Build failed! Please check the errors above.
    pause
    exit /b 1
)
echo     Build successful.
echo.

:: Run the Spring Boot application
echo [4/4] Starting application on http://localhost:%PORT%
echo     Press Ctrl+C to stop the server.
echo.
call "%MVN%" spring-boot:run -Dspring-boot.run.arguments=--server.port=%PORT%

:: If the application exits, wait for user input
pause
