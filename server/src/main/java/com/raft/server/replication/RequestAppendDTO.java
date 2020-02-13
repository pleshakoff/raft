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

    @NotNull
    private final Integer prevLogIndex;

    @NotNull
    private final Long prevLogTerm;

    @NotNull
    private final Integer leaderCommit;

    private final Operation operation;

}
