package com.raft.server.context;


import com.raft.server.node.Attributes;
import com.raft.server.node.State;
import com.raft.server.operations.OperationsLog;
import com.raft.server.node.peers.Peer;
import com.raft.server.node.peers.Peers;
import com.raft.server.node.term.Term;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.raft.server.node.State.FOLLOWER;

@Component
@Slf4j
@RequiredArgsConstructor
class ContextImpl implements Context {

    private final Peers peers;
    private final Term term;
    private final Attributes attributes;
    private final OperationsLog operationsLog;

    @Override
    public Integer getId() {
        return attributes.getId();
    }

    @Override
    public void setActive(Boolean active) {
        attributes.setActive(active);
    }

    @Override
    public Boolean getActive() {
        return attributes.getActive();
    }

    @Override
    public void cancelIfNotActive() {
        attributes.cancelIfNotActive();
    }

    @Override
    public State getState() {
        return attributes.getState();
    }

    @Override
    public void setState(State state) {
        attributes.setState(state);
    }

    @Override
    public Long getCurrentTerm() {
        return term.getCurrentTerm();
    }

    @Override
    public Integer getCommitIndex() {
        return attributes.getCommitIndex();
    }

    @Override
    public void setCommitIndex(Integer commitIndex) {
        attributes.setCommitIndex(commitIndex);
    }

    @Override
    public Integer getLastApplied() {
        return attributes.getLastApplied();
    }

    @Override
    public Integer getElectionTimeout() {
        return attributes.getElectionTimeout();
    }

    @Override
    public Integer getHeartBeatTimeout() {
        return attributes.getHeartBeatTimeout();
    }

    @Override
    public List<Peer> getPeers() {
        return peers.getPeers();
    }

    @Override
    public Peer getPeer(Integer id)  {
        return  peers.get(id);
    }

    @Override
    public Long incCurrentTerm() {
       return term.incCurrentTerm();
    }

    @Override
    public void setCurrentTerm(Long term) {
        this.term.setCurrentTerm(term);
    }

    @Override
    public Integer getLastIndexSize() {
        return operationsLog.getLastIndex();
    }

    @Override
    public Integer getQuorum() {
        return peers.getQuorum();
    }

    @Override
    public Integer getVotedFor() {
       return attributes.getVotedFor();
    }

    @Override
    public void setVotedFor(Integer votedFor) {
        attributes.setVotedFor(votedFor);
    }

    @Override
    public void setTermGreaterThenCurrent(Long term) {
        log.info("Peer #{} Get term {} greater then current. The current term is {}", getId(),term,getCurrentTerm());
        setState(FOLLOWER);
        setCurrentTerm(term);
        setVotedFor(null);
    }






}
