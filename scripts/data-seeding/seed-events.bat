@echo off
setlocal enabledelayedexpansion

echo 🚀 Custody Connect Test Data Seeder
echo ===================================
echo.

if "%1"=="help" (
    echo Usage: seed-events.bat [options]
    echo.
    echo Options:
    echo   mini     - Seed 10 diverse events ^(default^)
    echo   large    - Seed 100 events for stress testing
    echo   single   - Seed one event for testing
    echo   cleanup  - Remove all test data
    echo   verify   - Verify API connectivity and data
    echo   help     - Show this help message
    echo.
    echo Examples:
    echo   seed-events.bat mini
    echo   seed-events.bat large
    echo   seed-events.bat verify
    echo.
    pause
    exit /b 0
)

set "API_BASE=http://127.0.0.1:8080/api/v1"
set "MODE=%1"
if "%MODE%"=="" set "MODE=mini"

echo Mode: %MODE%
echo API Base: %API_BASE%
echo.

REM Check if backend is running
echo 🔍 Checking backend connectivity...
curl -s "%API_BASE%/health" >nul 2>&1
if errorlevel 1 (
    echo ❌ Backend not reachable at %API_BASE%/health
    echo Please ensure WildFly is running on port 8080
    pause
    exit /b 1
)
echo ✅ Backend is reachable

REM Run database migrations first
echo 🗄️  Running database migrations...
curl -s -X POST "%API_BASE%/admin/db/migrate" >nul 2>&1
if errorlevel 1 (
    echo ⚠️  Migration might have failed or already been run
) else (
    echo ✅ Database migrations completed
)

echo.

if "%MODE%"=="mini" (
    call :seed_mini
) else if "%MODE%"=="large" (
    call :seed_large
) else if "%MODE%"=="single" (
    call :seed_single
) else if "%MODE%"=="cleanup" (
    call :cleanup_data
) else if "%MODE%"=="verify" (
    call :verify_data
) else (
    echo ❌ Unknown mode: %MODE%
    echo Use 'seed-events.bat help' for usage information
    pause
    exit /b 1
)

echo.
echo ✅ Operation completed!
pause
exit /b 0

REM ==============================================
REM SEEDING FUNCTIONS
REM ==============================================

:seed_mini
echo 📦 Seeding mini dataset ^(10 events^)...
set "DATA_FILE=%~dp0mini-dataset-fixed.json"

if not exist "%DATA_FILE%" (
    echo ❌ Data file not found: %DATA_FILE%
    exit /b 1
)

set "SUCCESS_COUNT=0"
set "ERROR_COUNT=0"

for /f "delims=" %%i in ('type "%DATA_FILE%"') do (
    call :process_json_array "%%i"
)

echo 📊 Results: !SUCCESS_COUNT! successful, !ERROR_COUNT! errors
goto :eof

:seed_large
echo 📦 Seeding large dataset ^(100 events^)...
echo Generating synthetic data...

REM Create synthetic data using PowerShell
powershell -Command "
$types = @('CUSTODY_DEPOSIT_SETTLED', 'WITHDRAWAL_REQUESTED', 'SETTLEMENT_CONFIRMED', 'WITHDRAWAL_FAILED', 'LEDGER_ADJUSTMENT', 'CORPORATE_ACTION_DIVIDEND_POSTED', 'KYC_STATUS_UPDATED', 'TRADE_ALLOCATION', 'EVIDENCE_ATTACHED', 'SETTLEMENT_INSTRUCTED')
$sources = @('Euroclear', 'CustodyPlatform', 'SettlementEngine', 'Clearstream', 'CorporateActions', 'KYCService', 'TradingSystem', 'LedgerService', 'DocumentService', 'ExternalBank')
$assets = @('ISIN:LU1234567890', 'BTC', 'ETH', 'USDC', 'ISIN:DE0008404005', 'ISIN:US0378331005', 'GBP', 'EUR')

for ($i = 1; $i -le 100; $i++) {
    $type = $types[$i % $types.Length]
    $source = $sources[$i % $sources.Length]
    $asset = $assets[$i % $assets.Length]

    $payload = @{
        accountId = 'ACC-' + ('{0:000}' -f ($i % 25 + 1))
        asset = $asset
        quantity = [Math]::Round((Get-Random -Minimum 1 -Maximum 5000) / 10.0, 2)
        txId = 'TX-' + ('{0:00000}' -f $i)
        timestamp = (Get-Date).AddMinutes(-$i).ToString('s') + 'Z'
    }

    $event = @{
        type = $type
        sourceSystem = $source
        payload = ($payload | ConvertTo-Json -Compress)
    }

    $event | ConvertTo-Json -Compress
}" > "%TEMP%\synthetic-data.json"

set "SUCCESS_COUNT=0"
set "ERROR_COUNT=0"

for /f "delims=" %%i in ('type "%TEMP%\synthetic-data.json"') do (
    call :send_event "%%i"
)

echo 📊 Results: !SUCCESS_COUNT! successful, !ERROR_COUNT! errors
goto :eof

:seed_single
echo 🔹 Seeding single test event...
set "TEST_EVENT={\"type\":\"CUSTODY_DEPOSIT_SETTLED\",\"sourceSystem\":\"TestSystem\",\"payload\":\"{\\"accountId\\":\\"TEST-001\\",\\"asset\\":\\"BTC\\",\\"quantity\\":1.0,\\"txId\\":\\"TEST-001\\",\\"timestamp\\":\\"2025-10-19T12:00:00Z\\"}"}"

call :send_event "%TEST_EVENT%"
echo 📊 Results: !SUCCESS_COUNT! successful, !ERROR_COUNT! errors
goto :eof

:cleanup_data
echo 🧹 Cleaning up test data...
echo ⚠️  This will remove ALL events from the database!
echo Press Ctrl+C to cancel or any key to continue...
pause >nul

echo Deleting all events...
curl -s -X DELETE "%API_BASE%/admin/events/all" >nul 2>&1
if errorlevel 1 (
    echo ⚠️  Bulk delete endpoint not available, manual cleanup may be needed
) else (
    echo ✅ All events deleted
)
goto :eof

:verify_data
echo 🔍 Verifying API connectivity and data...
echo.

echo Testing health endpoint...
curl -s "%API_BASE%/health" | findstr "UP"
if errorlevel 1 (
    echo ❌ Health check failed
) else (
    echo ✅ Health check passed
)

echo.
echo Testing events count...
curl -s "%API_BASE%/events/count" | findstr "total"
if errorlevel 1 (
    echo ❌ Events count failed
) else (
    echo ✅ Events count successful
)

echo.
echo Testing events list...
curl -s "%API_BASE%/events?page=0&size=5" | findstr "items"
if errorlevel 1 (
    echo ❌ Events list failed
) else (
    echo ✅ Events list successful
)

echo.
echo Testing audit logs...
curl -s "%API_BASE%/audit?max=5" | findstr "action"
if errorlevel 1 (
    echo ❌ Audit logs failed
) else (
    echo ✅ Audit logs successful
)
goto :eof

REM ==============================================
REM HELPER FUNCTIONS
REM ==============================================

:process_json_array
set "json=%~1"
call :send_event "%json%"
goto :eof

:send_event
set "event_data=%~1"

echo Sending event...
curl -s -X POST "%API_BASE%/events" ^
  -H "Content-Type: application/json" ^
  -d "%event_data%" >nul 2>&1

if errorlevel 1 (
    set /a ERROR_COUNT+=1
    echo ❌ Failed to send event
) else (
    set /a SUCCESS_COUNT+=1
    echo ✅ Event sent successfully
)
goto :eof
