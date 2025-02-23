#!/bin/bash

# Function to stop the processes gracefully
function stop_instances {
  echo "Stopping both H2 instances..."
  kill $pid1 $pid2 $pid3 $pid4 $pid5 $pid6 $pid7 $pid8 $pid9 $pid10 $pid11 $pid12 $pid13 $pid14 $pid15 $pid16 $pid17 $pid18 $pid19 $pid20>/dev/null
  wait $pid1 $pid2 $pid3 $pid4 $pid5 $pid6 $pid7 $pid8 $pid9 $pid10 $pid11 $pid12 $pid13 $pid14 $pid15 $pid16 $pid17 $pid18 $pid19 $pid20>/dev/null
  echo "Both H2 instances have stopped."
}

# Trap Ctrl+C (SIGINT) and stop instances
trap stop_instances SIGINT

currentDir=$(pwd)

# Make Sure our databases are clean
cd ~
rm SIDIS-{authCommand_instance1_db,authCommand_instance2_db,authQuery_instance1_db,authQuery_instance2_db,bookCommand_instance1_db,bookCommand_instance2_db,bookQuery_instance1_db,bookQuery_instance2_db,lendingCommand_instance1_db,lendingCommand_instance2_db,lendingQuery_instance1_db,lendingQuery_instance2_db,readerCommand_instance1_db,readerCommand_instance2_db,readerQuery_instance1_db,readerQuery_instance2_db,acquisitionCommand_instance1_db,acquisitionCommand_instance2_db,acquisitionQuery_instance1_db,acquisitionQuery_instance2_db,recommendationCommand_instance1_db,recommendationCommand_instance2_db,recommendationQuery_instance1_db,recommendationQuery_instance2_db}.mv.db
touch SIDIS-{authCommand_instance1_db,authCommand_instance2_db,authQuery_instance1_db,authQuery_instance2_db,bookCommand_instance1_db,bookCommand_instance2_db,bookQuery_instance1_db,bookQuery_instance2_db,lendingCommand_instance1_db,lendingCommand_instance2_db,lendingQuery_instance1_db,lendingQuery_instance2_db,readerCommand_instance1_db,readerCommand_instance2_db,readerQuery_instance1_db,readerQuery_instance2_db,acquisitionCommand_instance1_db,acquisitionCommand_instance2_db,acquisitionQuery_instance1_db,acquisitionQuery_instance2_db,recommendationCommand_instance1_db,recommendationCommand_instance2_db,recommendationQuery_instance1_db,recommendationQuery_instance2_db}.mv.db

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
#authQuery_instance1_db
java -jar /home/breda/h2/bin/h2-2.3.232.jar -tcp -tcpPort 9099 -tcpAllowOthers > h2_instance9.log 2>&1 &
pid9=$!
#authQuery_instance2_db
java -jar /home/breda/h2/bin/h2-2.3.232.jar -tcp -tcpPort 9100 -tcpAllowOthers > h2_instance10.log 2>&1 &
pid10=$!
#bookQuery_instance1_db
java -jar /home/breda/h2/bin/h2-2.3.232.jar -tcp -tcpPort 9101 -tcpAllowOthers > h2_instance11.log 2>&1 &
pid11=$!
#bookQuery_instance2_db
java -jar /home/breda/h2/bin/h2-2.3.232.jar -tcp -tcpPort 9102 -tcpAllowOthers > h2_instance12.log 2>&1 &
pid12=$!
#lendingQuery_instance1_db
java -jar /home/breda/h2/bin/h2-2.3.232.jar -tcp -tcpPort 9103 -tcpAllowOthers > h2_instance13.log 2>&1 &
pid13=$!
#lendingQuery_instance2_db
java -jar /home/breda/h2/bin/h2-2.3.232.jar -tcp -tcpPort 9104 -tcpAllowOthers > h2_instance14.log 2>&1 &
pid14=$!
#readerQuery_instance1_db
java -jar /home/breda/h2/bin/h2-2.3.232.jar -tcp -tcpPort 9105 -tcpAllowOthers > h2_instance15.log 2>&1 &
pid15=$!
#readerQuery_instance2_db
java -jar /home/breda/h2/bin/h2-2.3.232.jar -tcp -tcpPort 9106 -tcpAllowOthers > h2_instance16.log 2>&1 &
pid16=$!
#acquisitionCommand_instance1_db
java -jar /home/breda/h2/bin/h2-2.3.232.jar -tcp -tcpPort 9107 -tcpAllowOthers > h2_instance16.log 2>&1 &
pid17=$!
#acquisitionCommand_instance2_db
java -jar /home/breda/h2/bin/h2-2.3.232.jar -tcp -tcpPort 9108 -tcpAllowOthers > h2_instance16.log 2>&1 &
pid18=$!
#acquisitionQuery_instance1_db
java -jar /home/breda/h2/bin/h2-2.3.232.jar -tcp -tcpPort 9109 -tcpAllowOthers > h2_instance16.log 2>&1 &
pid19=$!
#acquisitionQuery_instance2_db
java -jar /home/breda/h2/bin/h2-2.3.232.jar -tcp -tcpPort 9110 -tcpAllowOthers > h2_instance16.log 2>&1 &
pid20=$!
#recommendationCommand_instance1_db
java -jar /home/breda/h2/bin/h2-2.3.232.jar -tcp -tcpPort 9111 -tcpAllowOthers > h2_instance16.log 2>&1 &
pid21=$!
#recommendationCommand_instance2_db
java -jar /home/breda/h2/bin/h2-2.3.232.jar -tcp -tcpPort 9112 -tcpAllowOthers > h2_instance16.log 2>&1 &
pid22=$!
#recommendationQuery_instance1_db
java -jar /home/breda/h2/bin/h2-2.3.232.jar -tcp -tcpPort 9113 -tcpAllowOthers > h2_instance16.log 2>&1 &
pid23=$!
#recommendationQuery_instance2_db
java -jar /home/breda/h2/bin/h2-2.3.232.jar -tcp -tcpPort 9114 -tcpAllowOthers > h2_instance16.log 2>&1 &
pid24=$!

echo "Both H2 instances started"

# Wait for both processes to finish
wait $pid1 $pid2 $pid3 $pid4 $pid5 $pid6 $pid7 $pid8 $pid9 $pid10 $pid11 $pid12 $pid13 $pid14 $pid15 $pid16 $pid17 $pid18 $pid19 $pid20 $pid21 $pid22 $pid23 $pid24
