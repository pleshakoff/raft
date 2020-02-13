cd d:\jprojects\raft\server\
cmd /C gradlew assemble
java -jar D:\jprojects\raft\server\build\libs\raft-server.jar --raft.election-timeout=30 --raft.id=3 --server.port=8083