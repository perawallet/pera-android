#!/bin/sh
set -e

./gradlew koverHtmlReport
./gradlew koverXmlReport

./common-sdk/test-coverage/coverageValidator.sh
