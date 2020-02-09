package com.raft.server.context;

import java.util.List;

public interface Context {

    Integer getId();

    void setActive(Boolean active);

    Boolean getActive();

    State getState();

    void setState(State state);

    Integer getVotedFor();

    Long getCurrentTerm();

    void setCurrentTerm(long currentTerm);

    boolean checkTermGreaterThenCurrent(Long term);

    Long incCurrentTerm();

    Long getCommitIndex();

    void incCommitIndex();

    Long getLastApplied();

    void incLastApplied();

    void setVotedFor(Integer votedFor);

    Integer getElectionTimeout();

    List<Peer> getPeers();

    Integer getQuorum();

}
