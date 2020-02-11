package com.raft.server.replication;


import com.raft.server.data.Entry;
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

    private final Long prevLogIndex;
    private final Long prevLogTerm;
    private final Long leaderCommit;
    Entry entry;

}
