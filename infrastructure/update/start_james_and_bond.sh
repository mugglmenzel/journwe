#!/bin/bash -ex
# kill all screens
screen -ls | grep pts | cut -d. -f1 | awk '{print $1}' | xargs kill

# launch screens

sudo screen -S bond -dm -t bond bash -c 'sudo java -jar /home/ubuntu/james/conf/lib/journwe-james-0.1-SNAPSHOT-jar-with-dependencies.jar'
sudo screen -S bond -dm -t bond bash -c '(cd /home/ubuntu/james/bin/; sudo ./run.sh)'

