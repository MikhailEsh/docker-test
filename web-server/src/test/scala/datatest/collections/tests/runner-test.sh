#!/bin/sh

cd /etc/newman/datatest/collections/tests
for D in `find . -name  "*.json"`
do
    cd $(dirname "$D")
    newman run $(basename "$D") -e  /etc/newman/datatest/collections/common/local-docker-tets.postman_environment.json -r cli,junit --reporter-junit-export /etc/newman/datatest/collections/report/${D}.xml
    cd /etc/newman/datatest/collections/tests
done
#Дальше идет костыль для переименования testsuites на testsuite в отдельных xml отчетах, это нужно для корректных имен при формировании <отчет>.html
cd /etc/newman/datatest/collections/report
for D in `find . -name  "*.xml"`
do
    sed -i -e 's/testsuites/testsuite/g' "${D}"
done
junit-merge `find . -name  "*.xml"`
junit-viewer --results=merged-test-results.xml --save=merged-test-results.html