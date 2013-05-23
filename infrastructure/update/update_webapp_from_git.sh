#!/bin/sh

#cd $(dirname $0)
current_path=$(pwd)

ssh_opts_background=`echo -f -i $current_path/journwe.pem`
ssh_opts_terminal=`echo -t -i $current_path/journwe.pem`
scp_opts=`echo -i $current_path/journwe.pem`
ssh_host="ec2-user@$1"
#scp $scp_opts ./update_webapp_remote_from_git.sh $ssh_host:/home/ec2-user/ 
# ssh $ssh_opts_terminal $ssh_host "sudo su"
#ssh $ssh_opts_terminal $ssh_host "sudo sh /home/ec2-user/update_webapp_remote_from_git.sh"
cd $current_path/../../code/journwe-webapp/
echo "compiling staged play web app..."
play -Dconfig.file=conf/application-prod.conf clean compile dist
echo "copying play web app distribution to ec2 host..."
scp $scp_opts dist/journwe-*.zip $ssh_host:/home/ec2-user/ 
echo "unzipping..."
ssh $ssh_opts_terminal $ssh_host "unzip -q journwe-*.zip -d journwe"
echo "killing running java software..."
ssh $ssh_opts_terminal $ssh_host "killall java || true"
echo "starting play web server"
ssh $ssh_opts_terminal $ssh_host "journwe/start -Dhttp.port=80 >out 2>1 &"

