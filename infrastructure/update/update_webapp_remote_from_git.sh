#!/bin/sh

cd ~/journwe/code/journwe-webapp
git pull https://mugglmenzel:M21g14Z12@github.com/mugglmenzel/journwe.git
sudo sbt clean compile stage
if [ -f "RUNNING_PID" ];
then
 sudo kill `cat RUNNING_PID`
fi
sudo target/start -Dhttp.port=80


