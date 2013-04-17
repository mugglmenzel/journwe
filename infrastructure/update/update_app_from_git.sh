#!/bin/sh

cd $(dirname $0)

ssh_opts=`echo -t -i journwe.pem`
scp_opts=`echo -i journwe.pem`
ssh_host="ec2-user@ec2-184-73-40-7.compute-1.amazonaws.com"
scp $scp_opts ./update_webapp_remote_from_git.sh $ssh_host:/home/ec2-user/ 
ssh $ssh_opts $ssh_host "sh /home/ec2-user/update_webapp_remote_from_git.sh"
