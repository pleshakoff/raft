package com.raft.server.replication;

import org.springframework.scheduling.annotation.Async;

interface ReplicationService {


    @Async
    void heartBeat();

    AnswerAppendDTO append(RequestAppendDTO requestAppendDTO);


}
