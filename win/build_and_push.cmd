set version=%1
set service=%2

echo %service%
cd d:\jprojects\raft\%service%\
cmd /C gradlew assemble
docker image build -t pleshakoff/%service%:%version% .
docker image push pleshakoff/%service%:%version%

cd d:\jprojects\raft\win
