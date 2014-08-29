#!/bin/sh

[ $# -lt 1 ] && { echo "Usage: $0 <Load Balancer Name> [<first play cmd>]"; exit 1; }
[ "x$JAVA_HOME" == "x" ] && { echo "Need to set JAVA_HOME"; exit 1; }
#: ${JAVA_HOME:?"JAVA_HOME needs to be set!"}

cd $(dirname $0)
current_path=$(pwd)
#cd $current_path

ssh_opts_background=`echo -f -q -o "StrictHostKeyChecking=no" -i $current_path/journwe.pem`
ssh_opts_terminal=`echo -t -q -o "StrictHostKeyChecking=no" -i $current_path/journwe.pem`
scp_opts=`echo -i $current_path/journwe.pem`

#scp $scp_opts ./update_webapp_remote_from_git.sh $ssh_host:/home/ec2-user/ 
# ssh $ssh_opts_terminal $ssh_host "sudo su"
#ssh $ssh_opts_terminal $ssh_host "sudo sh /home/ec2-user/update_webapp_remote_from_git.sh"

echo "-----------------------------------------------------"
echo "--- JournWe Update Script for Ubuntu EC2 machines ---"
echo "-----------------------------------------------------"
echo ""
echo "-- compiling JournWe web app"
echo "compiling staged play web app..."
cd $current_path/../../code/journwe-webapp/
$current_path/../activator/activator -Dconfig.file=conf/application-prod.conf $2 compile dist

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

	echo "---- installing JournWe web app on $ipaddress"
	echo "----- copying play web app distribution to $ipaddress..."
	rsync -avzPu -e "ssh $scp_opts" target/universal/journwe-webapp-*.zip $ssh_host:/home/ubuntu/ 
        echo "----- stopping play web server..."
        ssh $ssh_opts_terminal $ssh_host "sudo initctl stop journwe"
	echo "----- unzipping..."
	ssh $ssh_opts_terminal $ssh_host "rm -r journwe"
	ssh $ssh_opts_terminal $ssh_host "unzip -q -o journwe-webapp-*.zip -d journwe"
	echo "----- chmoding..."
	ssh $ssh_opts_terminal $ssh_host "sudo chmod -R 777 journwe"
	echo "----- starting play web server..."
	#ssh $ssh_opts_terminal $ssh_host "sudo sh journwe/journwe-*/start -Dhttp.port=80 >out 2>&1 &"
	ssh $ssh_opts_terminal $ssh_host "sudo initctl start journwe"
	
	echo "---- finished updating $ipaddress"
done;

cd $current_path

echo "-----------------------------------------------------"
echo "------ JournWe Asset Distribution to CloudFront -----"
echo "-----------------------------------------------------"
echo ""
sh push_assets_to_s3.sh

echo "-- update complete."
