package com.raft.server.replication;

interface ReplicationService {


    AnswerAppendDTO append(RequestAppendDTO requestAppendDTO);


}
