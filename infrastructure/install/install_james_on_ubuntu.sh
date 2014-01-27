#!/bin/bash
echo "installing apache james..."
# fixing ec2 ubuntu 13 bug
sudo locale-gen en_US.UTF-8
echo "LC_ALL=\"en_US.UTF-8\"" | sudo tee -a /etc/environment
source /etc/environment
sudo apt-get update
# java7 has some jmx bug problems. use java6!
# Need Oracle Java for Bond Server
# which uses com.sun.net.httpserver
wget https://raw.github.com/markusklems/cassandra_training/master/cluster/install_java_from_url.sh
sudo sh install_java_from_url.sh 6 https://dl.dropboxusercontent.com/u/6336649/jre6u34-linux-x64.bin
# make sure the right version is installed: java -version
sudo apt-get install -qq libc6-i386 libc6-dev-i386
wget http://mirror.sdunix.com/apache//james/apache-james/3.0beta4/apache-james-3.0-beta4-app.tar.gz
tar xvfz apache-james*
rm *tar.gz
mv apache* james
cd james
sudo bin/james start
# check
sudo bin/james status
# check
sudo ./bin/james-cli.sh listdomains -h localhost -p 9999
# sudo ./bin/james-cli.sh adddomain journwe.com -h localhost -p 9999
# sudo ./bin/james-cli.sh listdomains -h localhost -p 9999
# sudo ./bin/james-cli.sh adduser markus@journwe.com geheim -h localhost -p 9999
# telnet 127.0.0.1 25
# ehlo journwe.com
# mail from:<markus@journwe.com>
# ... see example: http://james.apache.org/server/3/install.html and http://wiki.apache.org/james/JamesQuickstart