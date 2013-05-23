#!/bin/sh

#cd $(dirname $0)
current_path=$(pwd)

ssh_opts_background=`echo -f -i $current_path/journwe.pem`
ssh_opts_terminal=`echo -t -i $current_path/journwe.pem`
scp_opts=`echo -i $current_path/journwe.pem`
ssh_host="ubuntu@$1"
#scp $scp_opts ./update_webapp_remote_from_git.sh $ssh_host:/home/ec2-user/ 
# ssh $ssh_opts_terminal $ssh_host "sudo su"
#ssh $ssh_opts_terminal $ssh_host "sudo sh /home/ec2-user/update_webapp_remote_from_git.sh"
cd $current_path/../../code/journwe-webapp/

echo "-----------------------------------------------------"
echo "--- JournWe Update Script for Ubuntu EC2 machines ---"
echo "-----------------------------------------------------"
echo ""
echo "--- infiltrating EC2 machine $1"
echo "preparing machine..."
ssh $ssh_opts_terminal $ssh_host "sudo aptitude update"
ssh $ssh_opts_terminal $ssh_host "sudo aptitude install unzip"

echo "--- compiling JournWe web app"
echo "compiling staged play web app..."
play -Dconfig.file=conf/application-prod.conf clean compile dist
echo "copying play web app distribution to ec2 host..."
scp $scp_opts dist/journwe-*.zip $ssh_host:/home/ubuntu/ 

echo "--- installing JournWe web app"
echo "unzipping..."
ssh $ssh_opts_terminal $ssh_host "unzip -q -o journwe-*.zip -d journwe"
echo "chmoding..."
ssh $ssh_opts_terminal $ssh_host "sudo chmod -R 777 journwe"
echo "killing running java software..."
ssh $ssh_opts_terminal $ssh_host "sudo killall java || true"
echo "starting play web server..."
ssh $ssh_opts_terminal $ssh_host "sudo journwe/journwe-*/start -Dhttp.port=80 >out 2>1 &"

echo "--- update complete."
