@echo off
echo Starting React Frontend Development Server...
echo.

cd /d "%~dp0frontend"

if not exist "node_modules" (
    echo Installing dependencies...
    npm install
    echo.
)

echo Starting React dev server on http://localhost:3000
echo Backend proxy: http://127.0.0.1:8080
echo.

npm start
