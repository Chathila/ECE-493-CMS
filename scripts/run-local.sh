#!/usr/bin/env bash
set -euo pipefail

PORT="${1:-${PORT:-8080}}"

if ! command -v mvn >/dev/null 2>&1; then
  echo "Error: Maven (mvn) is not installed or not in PATH."
  exit 1
fi

if lsof -i ":${PORT}" >/dev/null 2>&1; then
  echo "Error: Port ${PORT} is already in use."
  echo "Use a different port: ./scripts/run-local.sh 8081"
  exit 1
fi

echo "Building app and preparing runtime dependencies..."
mvn -DskipTests package dependency:copy-dependencies

echo "Starting CMS on http://localhost:${PORT}/register"
exec java \
  -DPORT="${PORT}" \
  -cp "target/ece-493-cms-1.0-SNAPSHOT.jar:target/dependency/*" \
  com.ece493.cms.controller.CmsAppServer
