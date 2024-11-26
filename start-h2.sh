#!/bin/bash

# Function to stop the processes gracefully
function stop_instances {
  echo "Stopping both H2 instances..."
  kill $pid1 $pid2 $pid3 $pid4 $pid5 $pid6 $pid7 $pid8 2>/dev/null
  wait $pid1 $pid2 $pid3 $pid4 $pid5 $pid6 $pid7 $pid8 2>/dev/null
  echo "Both H2 instances have stopped."
}

# Trap Ctrl+C (SIGINT) and stop instances
trap stop_instances SIGINT

currentDir=$(pwd)

# Make Sure our databases are clean
cd ~
rm SIDIS-{auth_instance1_db,auth_instance2_db,book_instance1_db,book_instance2_db,lending_instance1_db,lending_instance2_db,reader_instance1_db,reader_instance2_db}.mv.db
touch SIDIS-{auth_instance1_db,auth_instance2_db,book_instance1_db,book_instance2_db,lending_instance1_db,lending_instance2_db,reader_instance1_db,reader_instance2_db}.mv.db

cd $currentDir

# Run the first H2 instance on port 9091 in the background and capture its PID
java -jar /home/breda/h2/bin/h2-2.3.232.jar -tcp -tcpPort 9091 -tcpAllowOthers > h2_instance1.log 2>&1 &
pid1=$!

# Run the second H2 instance on port 9095 in the background and capture its PID
java -jar /home/breda/h2/bin/h2-2.3.232.jar -tcp -tcpPort 9092 -tcpAllowOthers > h2_instance2.log 2>&1 &
pid2=$!

java -jar /home/breda/h2/bin/h2-2.3.232.jar -tcp -tcpPort 9093 -tcpAllowOthers > h2_instance3.log 2>&1 &
pid3=$!

java -jar /home/breda/h2/bin/h2-2.3.232.jar -tcp -tcpPort 9094 -tcpAllowOthers > h2_instance4.log 2>&1 &
pid4=$!

java -jar /home/breda/h2/bin/h2-2.3.232.jar -tcp -tcpPort 9095 -tcpAllowOthers > h2_instance5.log 2>&1 &
pid5=$!

java -jar /home/breda/h2/bin/h2-2.3.232.jar -tcp -tcpPort 9096 -tcpAllowOthers > h2_instance6.log 2>&1 &
pid6=$!

java -jar /home/breda/h2/bin/h2-2.3.232.jar -tcp -tcpPort 9097 -tcpAllowOthers > h2_instance7.log 2>&1 &
pid7=$!

java -jar /home/breda/h2/bin/h2-2.3.232.jar -tcp -tcpPort 9098 -tcpAllowOthers > h2_instance8.log 2>&1 &
pid8=$!

echo "Both H2 instances started"

# Wait for both processes to finish
wait $pid1 $pid2 $pid3 $pid4 $pid5 $pid6 $pid7 $pid8
