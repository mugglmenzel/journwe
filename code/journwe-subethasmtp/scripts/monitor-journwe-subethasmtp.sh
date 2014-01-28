#!/bin/bash

running=`ps aux | grep "java -jar journwe-subethasmtp"`

if [ -n "$running" ] ; then
val=1
else
val=0
fi

/usr/local/bin/aws cloudwatch put-metric-data --namespace "journwe-subethasmtp" --metric-name "smtp-health" --value $val

