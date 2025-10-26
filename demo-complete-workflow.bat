@echo off
echo 🚀 Custody Connect Complete Demo Workflow
echo ========================================
echo.
echo This script demonstrates the complete test data workflow:
echo 1. Verify API connectivity
echo 2. Validate test data format
echo 3. Seed test data
echo 4. Verify data in frontend
echo 5. Test filtering and pagination
echo.
pause

echo.
echo [STEP 1/5] Verifying API connectivity...
echo ========================================
call scripts\verification\test-api-endpoints.bat

echo.
echo [STEP 2/5] Validating test data format...
echo =========================================
node scripts/data-seeding/validate-data.js scripts/data-seeding/mini-dataset-fixed.json

echo.
echo [STEP 3/5] Seeding test data...
echo ==============================
call scripts/data-seeding/seed-events.bat mini

echo.
echo [STEP 4/5] Checking seeded data...
echo =================================
echo Testing events count...
curl -s "http://127.0.0.1:8080/api/v1/events/count"
echo.
echo Testing events list...
curl -s "http://127.0.0.1:8080/api/v1/events?page=0&size=3"
echo.
echo Testing audit logs...
curl -s "http://127.0.0.1:8080/api/v1/audit?max=5"
echo.

echo.
echo [STEP 5/5] Next steps...
echo =======================
echo.
echo 🎯 Now you can:
echo.
echo 1. Open the frontend: http://localhost:3000
echo    - Check Dashboard for event counts
echo    - Go to Events page to see all events
echo    - Test filtering by type/status
echo    - Test pagination
echo.
echo 2. Test different data sets:
echo    scripts\data-seeding\seed-events.bat large
echo    scripts\data-seeding\seed-events.bat single
echo.
echo 3. Clean up when done:
echo    scripts\data-seeding\seed-events.bat cleanup
echo.
echo ✅ Demo workflow complete!
echo.
echo 📚 For more options, see: scripts\README.md
echo.

pause
