package com.raft.server.replication;

interface ReplicationService {


    void sendAppendEntries();

    AnswerAppendDTO append(RequestAppendDTO requestAppendDTO);


}
