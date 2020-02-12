package com.raft.server.context.peers;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;


@RequiredArgsConstructor
@Slf4j
public class Peer {

    final Integer id;

    private final AtomicInteger nextIndex= new AtomicInteger(1);
    private final AtomicInteger matchIndex= new AtomicInteger(1);
    private final AtomicBoolean voteGranted=new AtomicBoolean(false);

    public Integer getId() {
        return this.id;
    }

    public Integer getNextIndex() {
        return this.nextIndex.get();
    }

    public void setNextIndex(Integer nextIndex) {
        this.nextIndex.set(nextIndex);
    }

    public void decNextIndex() {
        this.nextIndex.decrementAndGet();
    }

    public Integer getMatchIndex() {
        return this.matchIndex.get();
    }

    public Boolean getVoteGranted() {
        return this.voteGranted.get();
    }

    public void setMatchIndex(Integer matchIndex) {
        this.matchIndex.set(matchIndex);
    }

    public void setVoteGranted(Boolean voteGranted) {
        this.voteGranted.set(voteGranted);
    }
}
