@echo off
echo Starting Custody Connect Development Environment...
echo ==================================================
echo.

echo [1/2] Starting WildFly Backend Server...
echo.

cd /d "%~dp0"

if not exist "target\server" (
    echo ERROR: WildFly server not found in target\server
    echo Please run 'mvn clean package' first to build the project
    pause
    exit /b 1
)

if not exist "target\custody-connect.war" (
    echo ERROR: Application WAR not found in target\custody-connect.war
    echo Please run 'mvn clean package' first to build the project
    pause
    exit /b 1
)

echo Starting WildFly...
start "WildFly Backend" cmd /k "cd /d target\server\bin && standalone.bat"

echo Waiting for WildFly to start...
timeout /t 15 /nobreak > nul

echo.
echo [2/2] Starting React Frontend Development Server...
echo.

cd /d "%~dp0frontend"

if not exist "node_modules" (
    echo Installing frontend dependencies...
    npm install
)

echo.
echo ==================================================
echo Development servers starting:
echo.
echo Backend (WildFly): http://127.0.0.1:8080
echo   - API: http://127.0.0.1:8080/api/v1/health
echo   - Admin Console: http://127.0.0.1:9990
echo.
echo Frontend (React): http://localhost:3000
echo   - Proxy to backend: http://127.0.0.1:8080
echo.
echo ==================================================
echo.

start "React Frontend" cmd /k "npm start"

echo Both servers are starting in separate windows.
echo Close both windows to stop all servers.
echo.
pause
