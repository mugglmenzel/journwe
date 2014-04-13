#!/bin/bash

mvn jar:jar
mv target/*jar ../journwe-webapp/lib/.
