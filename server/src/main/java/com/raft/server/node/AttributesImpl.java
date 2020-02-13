package com.raft.server.node;


import com.raft.server.exceptions.NotActiveException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

import static com.raft.server.node.State.FOLLOWER;

@Component
@Slf4j
@RequiredArgsConstructor
class AttributesImpl implements Attributes {

    private final ApplicationEventPublisher applicationEventPublisher;

    @Value("${raft.id}")
    @Getter
    private Integer id;

    @Getter
    Boolean active = true;

    @Override
    public void setActive(Boolean active) {
        this.active = active;
        log.info("Peer #{} {}", getId(),active?"STARTED":"STOPPED");
    }

    private volatile State state = FOLLOWER;

    @Getter
    private volatile Integer votedFor = null;

    @Override
    public void setVotedFor(Integer votedFor) {
        this.votedFor = votedFor;
        log.debug("Peer #{} set voted for {}", getId(),votedFor);
    }

    private final AtomicInteger commitIndex = new AtomicInteger(-1);
    private final AtomicInteger lastApplied = new AtomicInteger(-1);

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
         this.state = state;
    }

    @Override
    public Integer getCommitIndex() {
        return commitIndex.get();
    }

    @Override
    public void setCommitIndex(Integer commitIndex) {
        this.commitIndex.set(commitIndex);
        log.info("Peer #{} New commit index: {}", getId(), this.commitIndex.get());
        applicationEventPublisher.publishEvent(new CommittedIndexChangedEvent(this));
    }

    @Override
    public Integer getLastApplied() {
        return lastApplied.get();
    }

    @Override
    public void incLastApplied() {
        log.info("Peer #{} New applied index: {}", getId(), this.lastApplied.incrementAndGet());
    }

    @Override
    public void cancelIfNotActive() {
        if (!getActive())
            throw  new NotActiveException();
    }






}
