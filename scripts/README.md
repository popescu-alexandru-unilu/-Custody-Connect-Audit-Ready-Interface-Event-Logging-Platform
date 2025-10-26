# 🚀 Custody Connect Test Data Tools

A comprehensive suite of tools for seeding, validating, and managing test data for the Custody Connect application.

## 📋 Table of Contents

- [Quick Start](#quick-start)
- [Data Seeding Tools](#data-seeding-tools)
- [Verification Tools](#verification-tools)
- [Data Validation](#data-validation)
- [Troubleshooting](#troubleshooting)

## 🚀 Quick Start

### Prerequisites
1. **Backend Running**: Ensure WildFly is running on `http://127.0.0.1:8080`
2. **Database**: H2 database should be accessible (handled automatically)

### One-Command Setup
```batch
# Start everything (backend + frontend)
start-all.bat

# Or seed test data directly
scripts\data-seeding\seed-events.bat mini
```

## 🛠️ Data Seeding Tools

### Windows Batch Script (`seed-events.bat`)

**Location**: `scripts/data-seeding/seed-events.bat`

**Usage**:
```batch
# Seed 10 diverse events (recommended for testing)
scripts\data-seeding\seed-events.bat mini

# Seed 100 events for stress testing
scripts\data-seeding\seed-events.bat large

# Seed single event for API testing
scripts\data-seeding\seed-events.bat single

# Verify API connectivity
scripts\data-seeding\seed-events.bat verify

# Clean all test data
scripts\data-seeding\seed-events.bat cleanup

# Show help
scripts\data-seeding\seed-events.bat help
```

### PowerShell Script (`seed-events.ps1`)

**Location**: `scripts/data-seeding/seed-events.ps1`

**Features**:
- Better error handling
- Progress indicators
- Verbose mode for debugging

**Usage**:
```powershell
# Basic usage (seeds 10 events)
.\scripts\data-seeding\seed-events.ps1

# Seed 100 events with verbose output
.\scripts\data-seeding\seed-events.ps1 -Mode large -Verbose

# Verify API connectivity
.\scripts\data-seeding\seed-events.ps1 -Mode verify

# Skip database migration (if already done)
.\scripts\data-seeding\seed-events.ps1 -SkipMigration
```

## 🔍 Verification Tools

### API Endpoint Tester (`test-api-endpoints.bat`)

**Location**: `scripts/verification/test-api-endpoints.bat`

**What it tests**:
- ✅ Health endpoint (`/api/v1/health`)
- ✅ Events count (`/api/v1/events/count`)
- ✅ Events list (`/api/v1/events`)
- ✅ Audit logs (`/api/v1/audit`)
- ✅ Database migration (`/api/v1/admin/db/migrate`)

**Usage**:
```batch
scripts\verification\test-api-endpoints.bat
```

## ✅ Data Validation

### Node.js Validator (`validate-data.js`)

**Location**: `scripts/data-seeding/validate-data.js`

**Features**:
- Validates JSON format
- Checks required fields (`type`, `sourceSystem`, `payload`)
- Validates event types against known values
- Checks payload JSON structure

**Usage**:
```batch
# Validate specific file
node scripts/data-seeding/validate-data.js scripts/data-seeding/mini-dataset-fixed.json

# Validate all JSON files in directory
node scripts/data-seeding/validate-data.js scripts/data-seeding/*.json
```

**Sample Output**:
```
🔍 Validating mini-dataset-fixed.json...
📊 Found 10 events to validate

✅ All validations passed!

📊 Summary: 0 errors, 0 warnings
```

## 📊 Test Data Formats

### Event Structure
All events must follow this structure:
```json
{
  "type": "CUSTODY_DEPOSIT_SETTLED",
  "sourceSystem": "Euroclear",
  "payload": "{\"accountId\":\"ACC-001\",\"asset\":\"BTC\",...}"
}
```

### Required Fields
- **`type`**: Event type (must be one of the predefined values)
- **`sourceSystem`**: Source system identifier
- **`payload`**: JSON string containing event data

### Supported Event Types
- `CUSTODY_DEPOSIT_SETTLED`
- `WITHDRAWAL_REQUESTED`
- `WITHDRAWAL_FAILED`
- `SETTLEMENT_INSTRUCTED`
- `SETTLEMENT_CONFIRMED`
- `CORPORATE_ACTION_DIVIDEND_POSTED`
- `KYC_STATUS_UPDATED`
- `TRADE_ALLOCATION`
- `LEDGER_ADJUSTMENT`
- `EVIDENCE_ATTACHED`

## 🔧 Configuration

### Environment Variables
- **Backend URL**: `http://127.0.0.1:8080/api/v1`
- **Frontend Port**: `3000` (configurable in `.env.local`)
- **Database**: H2 (embedded, configured via `application.properties`)

### File Locations
- **Test Data**: `scripts/data-seeding/mini-dataset-fixed.json`
- **Backend Logs**: `target/server/standalone/log/server.log`
- **Frontend Logs**: Console output when running `npm start`

## 🚨 Troubleshooting

### Common Issues

#### 1. "Backend not reachable"
**Error**: `❌ Backend not reachable at http://127.0.0.1:8080/api/v1/health`

**Solutions**:
```batch
# Start WildFly backend
start-all.bat

# Or manually start backend
cd target\server\bin
standalone.bat
```

#### 2. "Database migration failed"
**Error**: Migration errors during seeding

**Solutions**:
- Migration runs automatically, but you can run manually:
  ```batch
  curl -X POST "http://127.0.0.1:8080/api/v1/admin/db/migrate"
  ```
- Check database logs in WildFly console

#### 3. "Invalid JSON in payload"
**Error**: Data validation fails

**Solutions**:
```batch
# Validate your data first
node scripts/data-seeding/validate-data.js your-file.json

# Fix JSON escaping in payloads
# Ensure all quotes in payload are properly escaped
```

#### 4. "Port already in use"
**Error**: Port 8080 or 3000 already occupied

**Solutions**:
- Close other applications using these ports
- Check running processes:
  ```batch
  netstat -ano | findstr :8080
  netstat -ano | findstr :3000
  ```

### Debug Mode

#### Enable Verbose Logging
```powershell
# PowerShell with verbose output
.\scripts\data-seeding\seed-events.ps1 -Verbose

# Windows batch with detailed output
# (Modify the script to remove -s flag from curl commands)
```

#### Check API Responses
```batch
# Test individual endpoints
curl "http://127.0.0.1:8080/api/v1/health"
curl "http://127.0.0.1:8080/api/v1/events/count"
curl "http://127.0.0.1:8080/api/v1/events?page=0&size=5"
```

## 📈 Performance Tips

### For Large Datasets (100+ events)
1. **Use PowerShell version** for better performance
2. **Enable verbose mode** only when debugging
3. **Batch API calls** if hitting rate limits

### Database Optimization
- Events are automatically indexed on key fields
- Use pagination for large result sets (`?page=0&size=50`)
- Filter by status/type for better performance

## 🔄 Development Workflow

### Typical Testing Cycle
```batch
REM 1. Start the application
start-all.bat

REM 2. Verify everything is working
scripts\verification\test-api-endpoints.bat

REM 3. Seed test data
scripts\data-seeding\seed-events.bat mini

REM 4. Check data in frontend
# Open http://localhost:3000 and verify events appear

REM 5. Test filtering and pagination
# Use frontend to filter by type, status, etc.

REM 6. Clean up (optional)
scripts\data-seeding\seed-events.bat cleanup
```

### Adding New Event Types
1. Add to `validate-data.js` valid types array
2. Update this documentation
3. Test with validation script
4. Add sample payloads to test data

## 📞 Support

### Getting Help
1. **Check this README** first
2. **Run verification script**: `scripts\verification\test-api-endpoints.bat`
3. **Validate your data**: `node scripts/data-seeding/validate-data.js file.json`
4. **Check logs** in WildFly console and browser developer tools

### Reporting Issues
When reporting issues, include:
- Command you ran
- Full error output
- Steps to reproduce
- Whether backend was running (`scripts\verification\test-api-endpoints.bat` output)

---

**Happy Testing! 🎉**
