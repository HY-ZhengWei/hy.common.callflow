#!/bin/sh

cd ./bin


rm -R ./org/hy/common/callflow/junit


jar cvfm hy.common.callflow.jar MANIFEST.MF META-INF org

cp hy.common.callflow.jar ..
rm hy.common.callflow.jar
cd ..





cd ./src
jar cvfm hy.common.callflow-sources.jar MANIFEST.MF META-INF org
cp hy.common.callflow-sources.jar ..
rm hy.common.callflow-sources.jar
cd ..
