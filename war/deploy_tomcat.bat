@echo off
set tomcat="C:\Program Files\Apache Software Foundation\Tomcat 7.0\webapps"
set gdir=%tomcat%\game

REM stop service
net stop Tomcat7

REM remove previous deployed game
del %tomcat%\game.war
rmdir %gidr% /S /Q

REM deploy the new game
mkdir %gdir%
xcopy *.* %gdir% /E

REM start the service again
net start Tomcat7