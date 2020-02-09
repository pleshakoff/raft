package com.raft.server.election;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotNull;

@RequiredArgsConstructor
@Getter
public class RequestVoteDTO {
    @NotNull
    private final Long term;
    @NotNull
    private final Integer candidateId;
    @NotNull
    private final Long lastLogIndex;
    @NotNull
    private final Long lastLogTerm;
}
