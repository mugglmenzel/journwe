#!/bin/bash -ex

# 1. Create new bond jar file using Maven
# 2. Then scp the jar to the right directory on the server

JAMESBONDSERVER=${1:-54.221.210.118}

mvn clean compile assembly:single
scp -i ../../infrastructure/update/journwe.pem target/journwe-james-*-with-dependencies.jar ubuntu@$JAMESBONDSERVER:/home/ubuntu/james/conf/lib