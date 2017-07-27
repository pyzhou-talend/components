#@echo off
call mvn clean install

cd components-mongodb-definition\target
copy components-mongodb-definition-0.20.0-SNAPSHOT.jar D:\release\Talend-Studio-20170724_1932-V6.5.0SNAPSHOT\plugins\components-mongodb-definition-0.20.0-SNAPSHOT.jar 
if errorlevel 1 echo There happens some Error &pause&exit


pause
pause