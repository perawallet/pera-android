#!/bin/sh
set -e

# Generate HTML report for devs
./gradlew koverHtmlReport

# Generate XML report for coverage calculation
./gradlew koverXmlReport

./common-sdk/test-coverage/coverageValidator.sh
