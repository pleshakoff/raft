package com.raft.server.context;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;


@Getter
@RequiredArgsConstructor
public class Context {

    private final Integer id;
    private final Boolean active;
    private final State state;
    private final Integer votedFor;
    private final Integer commitIndex;
    private final Integer lastApplied;
    private final Integer electionTimeout;
    private final Integer heartBeatTimeout;
    private final AtomicLong currentTerm;
    private final List<Peer> peers;
    private final Integer lastIndex;

    public boolean isLeader() {
        return getState().equals(State.LEADER);
    }



}
