@echo off
setlocal

REM Function to stop the processes gracefully
:: :stop_instances
:: echo Stopping all H2 instances...
:: for %%p in (%pid1% %pid2% %pid3% %pid4% %pid5% %pid6% %pid7% %pid8%) do (
::     taskkill /PID %%p /F >nul 2>&1
:: )
:: echo All H2 instances have stopped.
:: exit /b

REM Define current directory
set currentDir=%cd%

REM Make sure databases are clean
cd "C:\Users\paulo\Documents\SIDIS\h2"

for %%f in (auth_instance1_db auth_instance2_db book_instance1_db book_instance2_db lending_instance1_db lending_instance2_db reader_instance1_db reader_instance2_db) do (
    del "%%f.mv.db" 2>nul
    type nul >"%%f.mv.db"
)

cd %currentDir%

REM Run H2 instances on different ports
set H2_JAR_PATH="C:\Program Files\H2\bin\h2-2.2.224.jar"
echo H2 path: %H2_JAR_PATH%

echo Starting H2 instances...

echo Starting H2 instance on port 9080
start /B java -jar %H2_JAR_PATH% -tcp -tcpPort 9090 -tcpAllowOthers > h2_instance1.log 2>&1

echo Starting H2 instance on port 9081
start /B java -jar %H2_JAR_PATH% -tcp -tcpPort 9091 -tcpAllowOthers > h2_instance2.log 2>&1

echo Starting H2 instance on port 9082
start /B java -jar %H2_JAR_PATH% -tcp -tcpPort 9092 -tcpAllowOthers > h2_instance3.log 2>&1

echo Starting H2 instance on port 9083
start /B java -jar %H2_JAR_PATH% -tcp -tcpPort 9093 -tcpAllowOthers > h2_instance4.log 2>&1

echo Starting H2 instance on port 9084
start /B java -jar %H2_JAR_PATH% -tcp -tcpPort 9094 -tcpAllowOthers > h2_instance5.log 2>&1

echo Starting H2 instance on port 9085
start /B java -jar %H2_JAR_PATH% -tcp -tcpPort 9095 -tcpAllowOthers > h2_instance6.log 2>&1

echo Starting H2 instance on port 9086
start /B java -jar %H2_JAR_PATH% -tcp -tcpPort 9096 -tcpAllowOthers > h2_instance7.log 2>&1

echo Starting H2 instance on port 9087
start /B java -jar %H2_JAR_PATH% -tcp -tcpPort 9097 -tcpAllowOthers > h2_instance8.log 2>&1

echo Starting H2 instance on port 9088
start /B java -jar %H2_JAR_PATH% -tcp -tcpPort 9098 -tcpAllowOthers > h2_instance9.log 2>&1

echo All H2 instances started

REM Check if ports are open
netstat -ano | find "9090"
netstat -ano | find "9091"
netstat -ano | find "9092"
netstat -ano | find "9093"
netstat -ano | find "9094"
netstat -ano | find "9095"
netstat -ano | find "9096"
netstat -ano | find "9097"
netstat -ano | find "9098"

REM Wait for all processes to finish (simulate wait for demonstration purposes)
pause
