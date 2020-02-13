package com.raft.server.replication;


import com.raft.server.operations.Operation;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotNull;

@RequiredArgsConstructor
@Getter
class RequestAppendDTO {

    @NotNull
    private final Long term;
    @NotNull
    private final Integer leaderId;

    private final Integer prevLogIndex;
    private final Long prevLogTerm;
    private final Integer leaderCommit;
    private final Operation operation;

}
