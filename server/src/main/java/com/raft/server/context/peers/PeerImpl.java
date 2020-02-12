package com.raft.server.context.peers;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;


@RequiredArgsConstructor
@Slf4j
public class PeerImpl implements Peer {

    final Integer id;

    private final AtomicInteger nextIndex= new AtomicInteger(0);
    private final AtomicInteger matchIndex= new AtomicInteger(-1);
    private final AtomicBoolean voteGranted=new AtomicBoolean(false);

    @Override
    public Integer getId() {
        return this.id;
    }

    @Override
    public Integer getNextIndex() {
        return this.nextIndex.get();
    }

    @Override
    public void setNextIndex(Integer nextIndex) {
        this.nextIndex.set(nextIndex);
    }

    @Override
    public void decNextIndex() {
        this.nextIndex.decrementAndGet();
    }

    @Override
    public Integer getMatchIndex() {
        return this.matchIndex.get();
    }

    @Override
    public Boolean getVoteGranted() {
        return this.voteGranted.get();
    }

    @Override
    public void setMatchIndex(Integer matchIndex) {
        this.matchIndex.set(matchIndex);
    }

    @Override
    public void setVoteGranted(Boolean voteGranted) {
        this.voteGranted.set(voteGranted);
    }
}
