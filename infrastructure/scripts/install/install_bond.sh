#!/bin/bash -ex
echo "#!/bin/bash -ex
# this belongs in /etc/init.d/bond

case \$1 in
    start)
        /bin/bash /usr/local/bin/bond-start.sh
    ;;
    stop)
        /bin/bash /usr/local/bin/bond-stop.sh
    ;;
    restart)
        /bin/bash /usr/local/bin/bond-stop.sh
        /bin/bash /usr/local/bin/bond-start.sh
    ;;
    status)
        /bin/bash /usr/local/bin/bond-status.sh
    ;;
esac
exit 0" | sudo tee /etc/init.d/bond

echo "#!/bin/bash
PIDFILE=/var/run/bond.pid

if [ -s \"\$PIDFILE\" ]; then
  echo \"Process file $PIDFILE already exists. It looks like the service is running already.\"
else
  sudo java -jar /home/ubuntu/james/conf/lib/journwe-james-0.1-SNAPSHOT-jar-with-dependencies.jar start & echo \$! | sudo tee /var/run/bond.pid
fi" | sudo tee /usr/local/bin/bond-start.sh

echo "#!/bin/bash
PIDFILE=/var/run/bond.pid

if [ -s \"\$PIDFILE\" ]; then
    sudo kill \`cat \$PIDFILE\`
    sudo rm \$PIDFILE 
else
    echo \"Process file \$PIDFILE does not exist! Are you sure the bond service is running?\"
fi" | sudo tee /usr/local/bin/bond-stop.sh


echo "#!/bin/bash
PIDFILE=/var/run/bond.pid

if [ -s \"\$PIDFILE\" ] ; then
  PORT=\`netstat -lt | grep 17766\`
  if [ -n \"\$PORT\" ] ; then
    echo \"running\"
  else
    echo \"terminated\"
  fi
else
  echo \"terminated\"
fi" | sudo tee /usr/local/bin/bond-status.sh

sudo update-rc.d bond defaults