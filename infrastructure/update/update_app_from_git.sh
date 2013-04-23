#!/bin/sh

cd $(dirname $0)

ssh_opts_background=`echo -f -i journwe.pem`
ssh_opts_terminal=`echo -t -i journwe.pem`
scp_opts=`echo -i journwe.pem`
ssh_host="ec2-user@ec2-184-73-40-7.compute-1.amazonaws.com"
scp $scp_opts ./update_webapp_remote_from_git.sh $ssh_host:/home/ec2-user/ 
# ssh $ssh_opts_terminal $ssh_host "sudo su"
ssh $ssh_opts_terminal $ssh_host "sudo sh /home/ec2-user/update_webapp_remote_from_git.sh"
