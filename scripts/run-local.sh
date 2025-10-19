#!/usr/bin/env bash

# Run Custody Connect locally (WildFly + WAR)
set -euo pipefail

echo "[run-local] Building WAR..."
MVN_CMD="mvn"; [ -f "mvnw" ] && chmod +x mvnw && MVN_CMD="./mvnw"
$MVN_CMD -q clean package

WAR="target/custody-connect.war"
if [ ! -f "$WAR" ]; then
  echo "[run-local] ERROR: WAR not found at $WAR" >&2
  exit 1
fi

echo "[run-local] Built $WAR"

# Deploy strategy A: copy to local WildFly if WILDFLY_HOME is set
if [ "${WILDFLY_HOME:-}" != "" ] && [ -d "$WILDFLY_HOME/standalone/deployments" ]; then
  echo "[run-local] Deploying to WildFly at $WILDFLY_HOME ..."
  cp -v "$WAR" "$WILDFLY_HOME/standalone/deployments/"
  echo "[run-local] Deployed. Ensure WildFly is running (standalone.sh) and listening on 8080."
else
  echo "[run-local] Skipping direct copy deploy (WILDFLY_HOME not set or deployments dir missing)."

  # Deploy strategy B: wildfly-maven-plugin with credentials
  if [ "${WILDFLY_USER:-}" != "" ] && [ "${WILDFLY_PASS:-}" != "" ]; then
    echo "[run-local] Attempting deploy via wildfly-maven-plugin ..."
    $MVN_CMD -q wildfly:deploy -Dwildfly.user="$WILDFLY_USER" -Dwildfly.pass="$WILDFLY_PASS" || {
      echo "[run-local] Maven deploy failed. Please deploy manually via admin console or CLI." >&2
    }
  else
    echo "[run-local] You can deploy manually: copy $WAR to <WILDFLY_HOME>/standalone/deployments/"
    echo "[run-local] Or set WILDFLY_USER/WILDFLY_PASS and rerun to use wildfly:deploy"
  fi
fi

cat <<'EOF'

[run-local] Verify backend endpoints:
  curl -i http://localhost:8080/api/v1/health
  curl -i "http://localhost:8080/api/v1/events/count"
  curl -i "http://localhost:8080/api/v1/audit?max=10"

[run-local] Frontend (in frontend/ directory):
  npm install
  npm start
  # Base URL is /api/v1 and CRA proxies to :8080 (see frontend/package.json).

EOF
