@echo off

setlocal enabledelayedexpansion

set PLAY_VERSION="2.2.2"
if not defined SBT_SCALA_VERSION set SBT_SCALA_VERSION=auto

if defined JPDA_PORT set DEBUG_PARAM="-Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=%JPDA_PORT%"

set p=%~dp0
set p=%p:\=/%
set fp=file:///!p: =%%20!

java -Xms512M -Xmx1024M -Xss1M -XX:+CMSClassUnloadingEnabled -XX:MaxPermSize=256M %DEBUG_PARAM% %JAVA_OPTS% -Dfile.encoding=UTF-8 -Dinput.encoding=Cp1252 -Dplay.version="%PLAY_VERSION%" -Dsbt.ivy.home="%~dp0..\repository" -Dplay.home="%~dp0." -Dsbt.boot.properties="%fp%sbt/sbt.boot.properties" -Dsbt.scala.version="%SBT_SCALA_VERSION%" %PLAY_OPTS% -jar "%~dp0sbt\sbt-launch.jar" %*

:end
endlocal
