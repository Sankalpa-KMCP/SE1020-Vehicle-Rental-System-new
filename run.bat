@echo off
setlocal EnableDelayedExpansion

REM ============================================================
REM  Vehicle Rental System - run / restart helper
REM
REM  - Kills any process currently listening on PORT (default 8081)
REM  - Starts the Spring Boot app using the bundled Maven
REM
REM  Usage:
REM    run.bat           -> uses port 8081
REM    run.bat 9090      -> uses port 9090
REM ============================================================

cd /d "%~dp0"

set "PORT=%~1"
if "%PORT%"=="" set "PORT=8081"

set "MVN=%~dp0apache-maven-3.9.6\bin\mvn.cmd"
if not exist "%MVN%" (
    echo [run] Bundled Maven not found at %MVN%
    echo [run] Falling back to "mvn" from PATH.
    set "MVN=mvn"
)

echo.
echo ===========================================================
echo  Vehicle Rental System - run.bat
echo  Port: %PORT%
echo ===========================================================
echo.

echo [run] Checking for process listening on port %PORT%...
for /f "delims=" %%K in ('powershell -NoProfile -Command ^
    "$p = Get-NetTCPConnection -LocalPort %PORT% -State Listen -ErrorAction SilentlyContinue | Select-Object -First 1 -ExpandProperty OwningProcess; if ($p) { try { Stop-Process -Id $p -Force -ErrorAction Stop; \"killed $p\" } catch { \"failed $p\" } } else { \"none\" }"') do (
    set "KILL_RESULT=%%K"
)

if "!KILL_RESULT!"=="none" (
    echo [run] No existing process on port %PORT%.
) else (
    echo [run] !KILL_RESULT! on port %PORT%.
    REM Give Windows a moment to release the socket
    timeout /t 2 /nobreak >nul
)

echo.
echo [run] Starting Spring Boot app on port %PORT%...
echo [run] Press Ctrl+C in this window to stop.
echo.

call "%MVN%" spring-boot:run -Dspring-boot.run.fork=false "-Dspring-boot.run.arguments=--server.port=%PORT%"

set "RC=%ERRORLEVEL%"
echo.
echo [run] mvn spring-boot:run exited with code %RC%.
endlocal & exit /b %RC%
