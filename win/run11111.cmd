cd d:\jprojects\raft\server\
cmd /C gradlew assemble
java -jar D:\jprojects\raft\server\build\libs\raft-server.jar --raft.election-timeout=10 --raft.id=1 --server.port=8081