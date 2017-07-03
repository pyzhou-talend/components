@ECHO OFF

TITLE COMPONENT WEB SERVICE
SET APP_CLASS="org.talend.components.service.rest.Application"

set THISDIR=%~dp0
SET CLASSPATH=.\config;.\config\default;${project.artifactId}-${project.version}.jar

REM Set env variables which points to hadoop winutils binaries. It is required for S3 component
SET HADOOP_HOME=%THISDIR%binaries\hadoop
SET PATH=%PATH%;%HADOOP_HOME%\bin

java %JAVA_OPTS% -Xmx2048m -Dfile.encoding=UTF-8 -Dorg.ops4j.pax.url.mvn.localRepository="%THISDIR%\.m2" -Dcomponent.default.config.folder="%THISDIR%\config\default" -cp %CLASSPATH% %APP_CLASS%
