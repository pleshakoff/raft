package com.raft.server.replication;


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
    private final Long prevLogIndex;
    @NotNull
    private final Long prevLogTerm;

    @NotNull
    private final Long leaderCommit;

    // entries[] log entries to store

}
