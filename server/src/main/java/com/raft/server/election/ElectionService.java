package com.raft.server.election;

import org.springframework.scheduling.annotation.Async;

public interface ElectionService {

    @Async
    void startElection();

    void interruptElection();

    void vote();
}
