#!/bin/sh

[ $# -lt 1 ] && { echo "Usage: $0 <Load Balancer Name> [<number of lines>]"; exit 1; }
[ "x$JAVA_HOME" == "x" ] && { echo "Need to set JAVA_HOME"; exit 1; }

numlines=${2:-30}

cd $(dirname $0)
current_path=$(pwd)

ssh_opts_background=`echo -f -q -o "StrictHostKeyChecking=no" -i $current_path/journwe.pem`
ssh_opts_terminal=`echo -t -q -o "StrictHostKeyChecking=no" -i $current_path/journwe.pem`
scp_opts=`echo -i $current_path/journwe.pem`

echo "-- preparing EC2 access"
export AWS_ELB_HOME=$current_path/../elastic-load-balancing
export EC2_HOME=$current_path/../ec2-api-tools
export PATH=$PATH:$AWS_ELB_HOME/bin:$EC2_HOME/bin
export AWS_CREDENTIAL_FILE=$AWS_ELB_HOME/credentials.properties
export AWS_ACCESS_KEY=AKIAJRJVMMAPP44IO3OA 
export AWS_SECRET_KEY=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  

echo "-- checking for available servers in load balancer $1 ..."
for instanceid in `elb-describe-instance-health $1 | awk '{print $2}'`; do
	ipaddress=`ec2-describe-instances $instanceid | grep INSTANCE | awk '{print $4}'`
	ssh_host="ubuntu@$ipaddress"
	
	echo "----- show application log of machine $ipaddress"
	ssh $ssh_opts_terminal $ssh_host "tail -n $numlines /home/ubuntu/journwe/journwe-webapp-1.0-SNAPSHOT/logs/application.log"
done;
