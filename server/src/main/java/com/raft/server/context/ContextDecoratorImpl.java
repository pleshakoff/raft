package com.raft.server.context;


import com.raft.server.exceptions.NotActiveException;
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
class ContextDecoratorImpl implements ContextDecorator {

    private final Peers peers;
    private final Term term;
    private final Context context;

//    @Value("${raft.id}")
//    @Getter
//    Integer id;
//

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
    public Long getCommitIndex() {
        return context.getCommitIndex();
    }

    @Override
    public void incCommitIndex() {
        context.incCommitIndex();
    }

    @Override
    public Long getLastApplied() {
        return context.getLastApplied();
    }

    @Override
    public void incLastApplied() {
        context.incLastApplied();
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
        return null;
    }

    @Override
    public void setVotedFor(Integer votedFor) {

    }


}
