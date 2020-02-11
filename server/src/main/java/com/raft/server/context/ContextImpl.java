package com.raft.server.context;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

import static com.raft.server.context.State.FOLLOWER;

@Component
@Slf4j
@RequiredArgsConstructor
class ContextImpl implements Context {


    @Value("${raft.id}")
    @Getter
    Integer id;

    @Setter
    @Getter
    Boolean active = true;

    private volatile State state = FOLLOWER;

    @Setter
    @Getter
    private volatile Integer votedFor = null;//TODO make persist

    private AtomicInteger commitIndex = new AtomicInteger(0);
    private AtomicInteger lastApplied = new AtomicInteger(0);

    @Value("${raft.election-timeout}")
    @Getter
    Integer electionTimeout;

    @Value("${raft.heartbeat-timeout}")
    @Getter
    Integer heartBeatTimeout;

    @Override
    public State getState() {
        return state;
    }

    @Override
    public void setState(State state) {
        log.info("Peer #{} Set new state: {}", getId(), state);
        synchronized (this) {
            this.state = state;
        }
    }

    @Override
    public Integer getCommitIndex() {
        return commitIndex.get();
    }

    @Override
    public void setCommitIndex(Integer commitIndex) {
        this.commitIndex.set(commitIndex);
        log.info("Peer #{} New commit index: {}", getId(), this.commitIndex.get());
    }

    @Override
    public Integer getLastApplied() {
        return lastApplied.get();
    }

    @Override
    public void setLastApplied(Integer lastApplied) {
        this.lastApplied.set(lastApplied);
        log.info("Peer #{} New applied index: {}", getId(), this.lastApplied.get());

    }


}
