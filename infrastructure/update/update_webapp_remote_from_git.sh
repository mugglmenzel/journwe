#!/bin/sh

cd ~/journwe/code/journwe-webapp
git pull https://mugglmenzel:M21g14Z12@github.com/mugglmenzel/journwe.git
sudo ~/play-2.1.0/play -Dconfig.resource=application-prod.conf clean compile stage
if [ -f "RUNNING_PID" ];
then
 sudo kill `cat RUNNING_PID`
fi
sudo nohup target/start -Dhttp.port=80 &


