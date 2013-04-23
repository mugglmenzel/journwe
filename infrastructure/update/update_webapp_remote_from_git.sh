#!/bin/sh

cd /home/ec2-user/journwe/code/journwe-webapp
git pull https://mugglmenzel:M21g14Z12@github.com/mugglmenzel/journwe.git
/home/ec2-user/play-2.1.0/play -Dconfig.resource=application-prod.conf clean compile stage
if [ -f "RUNNING_PID" ];
then
 kill `cat RUNNING_PID`
fi
nohup target/start -Dhttp.port=80 &



