set version=%1
set service=%2

echo %service%
cd d:\jprojects\raft\%service%\
cmd /C gradlew assemble
docker image build -t pleshakoff/raft-%service%:%version% .
docker image push pleshakoff/raft-%service%:%version%

cd d:\jprojects\raft\win
