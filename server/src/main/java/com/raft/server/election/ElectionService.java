package com.raft.server.election;

import org.springframework.scheduling.annotation.Async;

public interface ElectionService {

    @Async
    void processElection();

    void winElection(Long term);

    void loseElection(Long term);

    void interruptElection();

    void vote();
}
