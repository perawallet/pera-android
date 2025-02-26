#!/bin/sh
set -e

# Generate HTML report for devs
./gradlew koverHtmlReportCustomDebug

# Generate XML report for coverage calculation
./gradlew koverXmlReportCustomDebug

./common-sdk/test-coverage/coverageValidator.sh
