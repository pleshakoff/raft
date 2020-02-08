package com.raft.server.election;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class RequestVoteDTO {
    private final Long term;
    private final Integer candidateId;
    private final Long lastLogIndex;
    private final Long lastLogTerm;
}
