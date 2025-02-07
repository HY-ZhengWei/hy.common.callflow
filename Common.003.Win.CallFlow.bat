

del /Q hy.common.callflow.jar
del /Q hy.common.callflow-sources.jar


call mvn clean package
cd .\target\classes

rd /s/q .\org\hy\common\callflow\junit


jar cvfm hy.common.callflow.jar META-INF/MANIFEST.MF META-INF org

copy hy.common.callflow.jar ..\..
del /q hy.common.callflow.jar
cd ..\..





cd .\src\main\java
xcopy /S ..\resources\* .
jar cvfm hy.common.callflow-sources.jar META-INF\MANIFEST.MF META-INF org
copy hy.common.callflow-sources.jar ..\..\..
del /Q hy.common.callflow-sources.jar
rd /s/q META-INF
cd ..\..\..

pause