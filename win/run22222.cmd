cd d:\jprojects\raft\server\
cmd /C gradlew assemble
java -jar D:\jprojects\raft\server\build\libs\server.jar --raft.election-timeout=15 --raft.id=2 --server.port=8082