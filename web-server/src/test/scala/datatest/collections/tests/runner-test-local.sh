#!/bin/sh

cd /etc/newman/datatest/collections/tests
for D in `find . -name  "*.json"`
do
    newman run "${D}" -e  ../common/local-docker-tets.postman_environment.json -r cli,junit --reporter-junit-export ../../report/${D}.xml
done
cd ../../report
for D in `find . -name  "*.xml"`
do
    sed -i -e 's/testsuites/testsuite/g' "${D}"
done
junit-merge `find . -name  "*.xml"`
junit-viewer --results=merged-test-results.xml --save=merged-test-results.html