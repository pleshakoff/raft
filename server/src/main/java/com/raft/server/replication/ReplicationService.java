package com.raft.server.replication;

import com.raft.server.data.Operation;
import org.springframework.scheduling.annotation.Async;

public interface ReplicationService {


    @Async
    void appendRequest();

    AnswerAppendDTO append(RequestAppendDTO requestAppendDTO);


}
