package com.raft.server.election;

import org.springframework.scheduling.annotation.Async;

public interface ElectionService {


    @Async
    void processElection();
    AnswerVoteDTO vote(RequestVoteDTO requestVoteDTO);
}
