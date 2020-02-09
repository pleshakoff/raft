package com.raft.server.context;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static com.raft.server.context.State.FOLLOWER;

@Component
@Slf4j
@RequiredArgsConstructor
class ContextImpl implements Context {

    private final Peers peers;

    @Value("${raft.id}")
    @Getter
    Integer id;

    @Setter
    @Getter
    Boolean active = true;

    private volatile State state = FOLLOWER;
    private AtomicLong currentTerm = new AtomicLong(0L);//TODO make persist

    @Setter
    @Getter
    private Integer votedFor = null;//TODO make persist

    private AtomicLong commitIndex = new AtomicLong(0L);
    private AtomicLong lastApplied = new AtomicLong(0L);

    @Value("${raft.election-timeout}")
    @Getter
    Integer electionTimeout;

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
    public Long getCurrentTerm() {
        return currentTerm.get();
    }

    @Override
    public void setCurrentTerm(long currentTerm) {
        this.currentTerm.set(currentTerm);
        log.info("Peer #{} Set term to {}", getId(),getCurrentTerm());
    }

    public boolean checkTermGreaterThenCurrent(Long term) {
        if (term > getCurrentTerm()) {
            log.info("Peer #{} Get term {} greater then current. The current term is {}", getId(),term,getCurrentTerm());
            setState(FOLLOWER);
            setCurrentTerm(term);
            setVotedFor(null);
            return true;
        }
        return false;
    }

    @Override
    public Long incCurrentTerm() {
        long l = currentTerm.incrementAndGet();
        log.info("Peer #{} Term incremented: {}", getId(), currentTerm.get());
        return l;
    }

    @Override
    public Long getCommitIndex() {
        return commitIndex.get();
    }

    @Override
    public void incCommitIndex() {
        commitIndex.incrementAndGet();
    }

    ;

    @Override
    public Long getLastApplied() {
        return lastApplied.get();
    }

    @Override
    public void incLastApplied() {
        lastApplied.incrementAndGet();
    }

    @Override
    public List<Peer> getPeers() {
        return peers.getPeers();
    }

    @Override
    public Integer getQuorum() {
        return peers.getQuorum();
    }

    ;
}
