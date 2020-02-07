package com.raft.server.node;

public interface Context {

    Integer getId();

    void setActive(Boolean active);

    Boolean getActive();

    State getState();

    void setState(State state);

    Integer getVotedFor();

    Long getCurrentTerm();

    void incCurrentTerm();

    Long getCommitIndex();

    void incCommitIndex();

    Long getLastApplied();

    void incLastApplied();

    void setVotedFor(Integer votedFor);

    Integer getElectionTimeout();
}
