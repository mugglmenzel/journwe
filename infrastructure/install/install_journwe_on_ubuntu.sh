#!/bin/sh -ex
## Prepare ubuntu ##
export DEBIAN_FRONTEND=noninteractive
ubuntuname=$(sudo cat /etc/lsb-release | echo `grep DISTRIB_CODENAME` | sed 's/DISTRIB_CODENAME=//')
echo "deb http://archive.canonical.com/ubuntu $ubuntuname partner" | sudo tee -a /etc/apt/sources.list.d/java.sources.list
sudo echo "sun-java6-bin shared/accepted-sun-dlj-v1-1 boolean true" | sudo debconf-set-selections
sudo apt-get update -y

## Remove openjdk ##
sudo apt-get purge -y openjdk-6-jre-lib
sudo apt-get purge -y openjdk-7-jre openjdk-7-jre-lib
sudo apt-get autoremove -y
sudo apt-get update -y

## Install sun jdk ##
target_java_dir='/opt/java/64'
sudo mkdir -p $target_java_dir
url=http://download.oracle.com/otn-pub/java/jdk/6u35-b10/jdk-6u35-linux-x64.bin
tmpdir=`sudo mktemp -d`
# Silent download without Oracle licenses hassling us (hopefully).
sudo wget -c --no-cookies --header "Cookie: gpw_e24=http%3A%2F%2Fwww.oracle.com%2F" "$url" --output-document="$tmpdir/`basename $url`"
sudo chmod 777 $tmpdir
(cd $tmpdir; sudo sh `basename $url` -noregister)
sudo mkdir -p `dirname $target_java_dir`
(cd $tmpdir; sudo mv jdk1* $target_java_dir)
sudo rm -rf $tmpdir
# Setup java alternatives.
sudo update-alternatives --install /usr/bin/java java "$target_java_dir/jdk1.6.0_35/bin/java" 17000
sudo update-alternatives --set java "$target_java_dir/jdk1.6.0_35/bin/java"
sudo update-alternatives --install /usr/bin/javac javac "$target_java_dir/jdk1.6.0_35/bin/javac" 17001
sudo update-alternatives --set javac "$target_java_dir/jdk1.6.0_35/bin/javac"
# Set java paths
export JAVA_HOME="$target_java_dir/jdk1.6.0_35"
if [ -f /etc/profile ]; then
  echo export JAVA_HOME=$JAVA_HOME >> /etc/profile
fi
if [ -f /etc/bashrc ]; then
  echo export JAVA_HOME=$JAVA_HOME >> /etc/bashrc
fi
if [ -f ~root/.bashrc ]; then
  echo export JAVA_HOME=$JAVA_HOME >> ~root/.bashrc
fi
if [ -f /etc/skel/.bashrc ]; then
  echo export JAVA_HOME=$JAVA_HOME >> /etc/skel/.bashrc
fi

## Install software packages using apt-get ##
sudo apt-get -y install git-core

## Setup play and compile journwe ##
JOURNWE_HOME=`(cd ../../; pwd)`

## Scala build tools ##
wget http://apt.typesafe.com/repo-deb-build-0002.deb
sudo dpkg -i repo-deb-build-0002.deb
sudo apt-get update
sudo apt-get install typesafe-stack -qq
wget http://typesafe.artifactoryonline.com/typesafe/ivy-releases/org.scala-sbt/sbt-launch/0.12.2/sbt-launch.jar
mkdir -p /home/ubuntu/.sbt/.lib/0.12.2
mv sbt-launch.jar /home/ubuntu/.sbt/.lib/0.12.2/sbt-launch.jar

## compile and run server ##
(cd "$JOURNWE_HOME/code/journwe-webapp"; sudo sbt clean compile stage)
sudo "./$JOURNWE_HOME/code/journwe-webapp/target/start" -Dhttp.port=80 &