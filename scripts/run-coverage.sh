#!/usr/bin/env bash
set -euo pipefail

if ! command -v mvn >/dev/null 2>&1; then
  echo "Error: Maven (mvn) is not installed or not in PATH."
  exit 1
fi

echo "Running test suite + JaCoCo coverage checks..."
mvn clean verify

REPORT_HTML="target/site/jacoco/index.html"
REPORT_XML="target/site/jacoco/jacoco.xml"

if [[ -f "${REPORT_XML}" ]]; then
  BRANCH_LINE="$(
    python3 - <<'PY'
import xml.etree.ElementTree as ET
root = ET.parse("target/site/jacoco/jacoco.xml").getroot()
for c in root.findall("counter"):
    if c.attrib["type"] == "BRANCH":
        missed = int(c.attrib["missed"])
        covered = int(c.attrib["covered"])
        ratio = 100.0 * covered / (covered + missed) if (covered + missed) else 100.0
        print(f"Branch coverage: {ratio:.2f}% (missed={missed}, covered={covered})")
        break
PY
  )"
  echo "${BRANCH_LINE}"
fi

echo "Coverage reports:"
echo "  HTML: ${REPORT_HTML}"
echo "  XML : ${REPORT_XML}"
echo "Open the HTML report in your browser to inspect details."
