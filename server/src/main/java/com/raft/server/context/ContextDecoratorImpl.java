package com.raft.server.context;


import com.raft.server.exceptions.NotActiveException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.raft.server.context.State.FOLLOWER;

@Component
@Slf4j
@RequiredArgsConstructor
class ContextDecoratorImpl implements ContextDecorator {

    private final Peers peers;
    private final Term term;
    private final Context context;

    @Override
    public Integer getId() {
        return context.getId();
    }

    @Override
    public void setActive(Boolean active) {
        context.setActive(active);

    }

    @Override
    public Boolean getActive() {
        return context.getActive();
    }

    @Override
    public void cancelIfNotActive() {
        if (!getActive())
            throw  new NotActiveException();
    }

    @Override
    public State getState() {
        return context.getState();
    }

    @Override
    public void setState(State state) {
        context.setState(state);
    }

    @Override
    public Long getCurrentTerm() {
        return term.getCurrentTerm();
    }

    @Override
    public Integer getCommitIndex() {
        return context.getCommitIndex();
    }

    @Override
    public void setCommitIndex(Integer commitIndex) {
        context.setCommitIndex(commitIndex);
    }

    @Override
    public Integer getLastApplied() {
        return context.getLastApplied();
    }

    @Override
    public void setLastApplied(Integer lastApplied) {
        context.setLastApplied(lastApplied);
    }

    @Override
    public Integer getElectionTimeout() {
        return null;
    }

    @Override
    public Integer getHeartBeatTimeout() {
        return null;
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
    public void setTermGreaterThenCurrent(Long term) {
            log.info("Peer #{} Get term {} greater then current. The current term is {}", getId(),term,getCurrentTerm());
            setState(FOLLOWER);
            setCurrentTerm(term);
            setVotedFor(null);
    }

    @Override
    public Integer getQuorum() {
        return peers.getQuorum();
    }

    @Override
    public Integer getVotedFor() {
       return context.getVotedFor();
    }

    @Override
    public void setVotedFor(Integer votedFor) {
        context.setVotedFor(votedFor);
    }




}
