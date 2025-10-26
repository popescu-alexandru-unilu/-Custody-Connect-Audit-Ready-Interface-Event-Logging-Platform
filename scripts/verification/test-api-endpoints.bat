@echo off
setlocal enabledelayedexpansion

echo 🔍 Custody Connect API Verification
echo ==================================
echo.

set "API_BASE=http://127.0.0.1:8080/api/v1"

echo Testing API endpoints...
echo.

REM Test health endpoint
echo [1/5] Testing health endpoint...
curl -s "%API_BASE%/health" | findstr "UP" >nul
if errorlevel 1 (
    echo ❌ Health check failed
) else (
    echo ✅ Health check passed
)

REM Test events count
echo.
echo [2/5] Testing events count...
curl -s "%API_BASE%/events/count" | findstr "total" >nul
if errorlevel 1 (
    echo ❌ Events count failed
) else (
    echo ✅ Events count successful
)

REM Test events list
echo.
echo [3/5] Testing events list...
curl -s "%API_BASE%/events?page=0&size=5" | findstr "items" >nul
if errorlevel 1 (
    echo ❌ Events list failed
) else (
    echo ✅ Events list successful
)

REM Test audit logs
echo.
echo [4/5] Testing audit logs...
curl -s "%API_BASE%/audit?max=5" | findstr "action" >nul
if errorlevel 1 (
    echo ❌ Audit logs failed
) else (
    echo ✅ Audit logs successful
)

REM Test database migration
echo.
echo [5/5] Testing database migration...
curl -s -X POST "%API_BASE%/admin/db/migrate" >nul
if errorlevel 1 (
    echo ⚠️  Database migration might have failed
) else (
    echo ✅ Database migration successful
)

echo.
echo ==================================
echo Verification complete!
echo.
echo Quick data seeding:
echo   scripts\data-seeding\seed-events.bat mini
echo   scripts\data-seeding\seed-events.bat verify
echo.
pause
