param(
    [Parameter(Mandatory=$false)]
    [ValidateSet("mini", "large", "single", "cleanup", "verify", "help")]
    [string]$Mode = "mini",

    [Parameter(Mandatory=$false)]
    [string]$ApiBase = "http://127.0.0.1:8080/api/v1",

    [Parameter(Mandatory=$false)]
    [switch]$SkipMigration,

    [Parameter(Mandatory=$false)]
    [switch]$Verbose
)

# Display help if requested
if ($Mode -eq "help") {
    Write-Host "🚀 Custody Connect Test Data Seeder (PowerShell Edition)" -ForegroundColor Green
    Write-Host "=====================================================" -ForegroundColor Green
    Write-Host ""
    Write-Host "Usage: .\seed-events.ps1 [options]" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "Parameters:" -ForegroundColor Cyan
    Write-Host "  -Mode        : mini, large, single, cleanup, verify (default: mini)" -ForegroundColor White
    Write-Host "  -ApiBase     : API base URL (default: http://127.0.0.1:8080/api/v1)" -ForegroundColor White
    Write-Host "  -SkipMigration : Skip database migration step" -ForegroundColor White
    Write-Host "  -Verbose     : Show detailed output" -ForegroundColor White
    Write-Host ""
    Write-Host "Examples:" -ForegroundColor Cyan
    Write-Host "  .\seed-events.ps1 -Mode mini -Verbose" -ForegroundColor White
    Write-Host "  .\seed-events.ps1 -Mode large" -ForegroundColor White
    Write-Host "  .\seed-events.ps1 -Mode verify -ApiBase 'http://localhost:8080/api/v1'" -ForegroundColor White
    Write-Host ""
    pause
    exit 0
}

Write-Host "🚀 Custody Connect Test Data Seeder (PowerShell)" -ForegroundColor Green
Write-Host "==============================================" -ForegroundColor Green
Write-Host ""

Write-Host "Mode: $Mode" -ForegroundColor Cyan
Write-Host "API Base: $ApiBase" -ForegroundColor Cyan
Write-Host ""

# Check if backend is running
Write-Host "🔍 Checking backend connectivity..." -ForegroundColor Yellow
try {
    $healthResponse = Invoke-RestMethod -Uri "$ApiBase/health" -Method Get -TimeoutSec 5
    Write-Host "✅ Backend is reachable" -ForegroundColor Green
} catch {
    Write-Host "❌ Backend not reachable at $ApiBase/health" -ForegroundColor Red
    Write-Host "Please ensure WildFly is running on port 8080" -ForegroundColor Red
    pause
    exit 1
}

# Run database migrations
if (-not $SkipMigration) {
    Write-Host "🗄️  Running database migrations..." -ForegroundColor Yellow
    try {
        $migrationResponse = Invoke-RestMethod -Uri "$ApiBase/admin/db/migrate" -Method Post -TimeoutSec 10
        Write-Host "✅ Database migrations completed" -ForegroundColor Green
    } catch {
        Write-Host "⚠️  Migration might have failed or already been run" -ForegroundColor Yellow
    }
}

Write-Host ""

# Execute based on mode
switch ($Mode) {
    "mini" { Invoke-SeedMini }
    "large" { Invoke-SeedLarge }
    "single" { Invoke-SeedSingle }
    "cleanup" { Invoke-Cleanup }
    "verify" { Invoke-Verify }
}

Write-Host ""
Write-Host "✅ Operation completed!" -ForegroundColor Green
pause

# ==============================================
# SEEDING FUNCTIONS
# ==============================================

function Invoke-SeedMini {
    Write-Host "📦 Seeding mini dataset (10 events)..." -ForegroundColor Cyan

    $dataFile = Join-Path $PSScriptRoot "mini-dataset-fixed.json"
    if (-not (Test-Path $dataFile)) {
        Write-Host "❌ Data file not found: $dataFile" -ForegroundColor Red
        return
    }

    $events = Get-Content $dataFile | ConvertFrom-Json
    $successCount = 0
    $errorCount = 0

    foreach ($event in $events) {
        try {
            $response = Invoke-RestMethod -Uri "$ApiBase/events" -Method Post -ContentType "application/json" -Body (ConvertTo-Json $event -Depth 10) -TimeoutSec 10
            $successCount++
            if ($Verbose) {
                Write-Host "✅ $($event.type) from $($event.sourceSystem)" -ForegroundColor Green
            }
        } catch {
            $errorCount++
            Write-Host "❌ Failed to send $($event.type): $($_.Exception.Message)" -ForegroundColor Red
        }
    }

    Write-Host "📊 Results: $successCount successful, $errorCount errors" -ForegroundColor Cyan
}

function Invoke-SeedLarge {
    Write-Host "📦 Seeding large dataset (100 events)..." -ForegroundColor Cyan
    Write-Host "Generating synthetic data..." -ForegroundColor Yellow

    $types = @('CUSTODY_DEPOSIT_SETTLED', 'WITHDRAWAL_REQUESTED', 'SETTLEMENT_CONFIRMED', 'WITHDRAWAL_FAILED', 'LEDGER_ADJUSTMENT', 'CORPORATE_ACTION_DIVIDEND_POSTED', 'KYC_STATUS_UPDATED', 'TRADE_ALLOCATION', 'EVIDENCE_ATTACHED', 'SETTLEMENT_INSTRUCTED')
    $sources = @('Euroclear', 'CustodyPlatform', 'SettlementEngine', 'Clearstream', 'CorporateActions', 'KYCService', 'TradingSystem', 'LedgerService', 'DocumentService', 'ExternalBank')
    $assets = @('ISIN:LU1234567890', 'BTC', 'ETH', 'USDC', 'ISIN:DE0008404005', 'ISIN:US0378331005', 'GBP', 'EUR')

    $successCount = 0
    $errorCount = 0

    for ($i = 1; $i -le 100; $i++) {
        $type = $types[$i % $types.Length]
        $source = $sources[$i % $sources.Length]
        $asset = $assets[$i % $assets.Length]

        $payload = @{
            accountId = "ACC-{0:000}" -f ($i % 25 + 1)
            asset = $asset
            quantity = [Math]::Round((Get-Random -Minimum 1 -Maximum 5000) / 10.0, 2)
            txId = "TX-{0:00000}" -f $i
            timestamp = (Get-Date).AddMinutes(-$i).ToString('s') + 'Z'
        }

        $event = @{
            type = $type
            sourceSystem = $source
            payload = ConvertTo-Json $payload -Compress
        }

        try {
            $response = Invoke-RestMethod -Uri "$ApiBase/events" -Method Post -ContentType "application/json" -Body (ConvertTo-Json $event -Depth 10) -TimeoutSec 10
            $successCount++
            if ($Verbose -and ($i % 10 -eq 0)) {
                Write-Host "✅ Processed $i events..." -ForegroundColor Green
            }
        } catch {
            $errorCount++
            if ($Verbose) {
                Write-Host "❌ Failed at event $i $($event.type): $($_.Exception.Message)" -ForegroundColor Red
            }
        }
    }

    Write-Host "📊 Results: $successCount successful, $errorCount errors" -ForegroundColor Cyan
}

function Invoke-SeedSingle {
    Write-Host "🔹 Seeding single test event..." -ForegroundColor Cyan

    $event = @{
        type = "CUSTODY_DEPOSIT_SETTLED"
        sourceSystem = "TestSystem"
        payload = ConvertTo-Json @{
            accountId = "TEST-001"
            asset = "BTC"
            quantity = 1.0
            txId = "TEST-001"
            timestamp = (Get-Date).ToString('s') + 'Z'
        } -Compress
    }

    try {
        $response = Invoke-RestMethod -Uri "$ApiBase/events" -Method Post -ContentType "application/json" -Body (ConvertTo-Json $event -Depth 10) -TimeoutSec 10
        Write-Host "✅ Test event sent successfully" -ForegroundColor Green
    } catch {
        Write-Host "❌ Failed to send test event: $($_.Exception.Message)" -ForegroundColor Red
    }
}

function Invoke-Cleanup {
    Write-Host "🧹 Cleaning up test data..." -ForegroundColor Yellow
    Write-Host "⚠️  This will remove ALL events from the database!" -ForegroundColor Red
    Write-Host "Press Ctrl+C to cancel or any key to continue..." -ForegroundColor Red
    $null = Read-Host

    try {
        $response = Invoke-RestMethod -Uri "$ApiBase/admin/events/all" -Method Delete -TimeoutSec 10
        Write-Host "✅ All events deleted" -ForegroundColor Green
    } catch {
        Write-Host "⚠️  Bulk delete endpoint not available, manual cleanup may be needed" -ForegroundColor Yellow
    }
}

function Invoke-Verify {
    Write-Host "🔍 Verifying API connectivity and data..." -ForegroundColor Cyan
    Write-Host ""

    # Test health endpoint
    Write-Host "Testing health endpoint..." -ForegroundColor Yellow
    try {
        $health = Invoke-RestMethod -Uri "$ApiBase/health" -Method Get -TimeoutSec 5
        Write-Host "✅ Health check passed: $($health.status)" -ForegroundColor Green
    } catch {
        Write-Host "❌ Health check failed: $($_.Exception.Message)" -ForegroundColor Red
    }

    Write-Host ""

    # Test events count
    Write-Host "Testing events count..." -ForegroundColor Yellow
    try {
        $count = Invoke-RestMethod -Uri "$ApiBase/events/count" -Method Get -TimeoutSec 5
        Write-Host "✅ Events count: $($count.total)" -ForegroundColor Green
    } catch {
        Write-Host "❌ Events count failed: $($_.Exception.Message)" -ForegroundColor Red
    }

    Write-Host ""

    # Test events list
    Write-Host "Testing events list..." -ForegroundColor Yellow
    try {
        $events = Invoke-RestMethod -Uri "$ApiBase/events?page=0&size=5" -Method Get -TimeoutSec 5
        $eventCount = if ($events.items) { $events.items.Count } else { 0 }
        Write-Host "✅ Events list: $eventCount events retrieved" -ForegroundColor Green
    } catch {
        Write-Host "❌ Events list failed: $($_.Exception.Message)" -ForegroundColor Red
    }

    Write-Host ""

    # Test audit logs
    Write-Host "Testing audit logs..." -ForegroundColor Yellow
    try {
        $audit = Invoke-RestMethod -Uri "$ApiBase/audit?max=5" -Method Get -TimeoutSec 5
        $auditCount = if ($audit) { @($audit).Count } else { 0 }
        Write-Host "✅ Audit logs: $auditCount entries retrieved" -ForegroundColor Green
    } catch {
        Write-Host "❌ Audit logs failed: $($_.Exception.Message)" -ForegroundColor Red
    }
}
