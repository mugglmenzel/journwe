#!/bin/bash

(cd ../../code/journwe-james; mvn clean compile assembly:single)
scp -i journwe.pem ../../code/journwe-james/target//journwe-james-*-with-dependencies.jar ubuntu@54.221.210.118:/home/ubuntu/james/conf/lib/.