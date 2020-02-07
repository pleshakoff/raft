package com.raft.server.node;

public interface NodeState {
    State getState();

    Long getCurrentTerm();

    void incCurrentTerm();

    Long getCommitIndex();

    void incCommitIndex();

    Long getLastApplied();

    void incLastApplied();

    void setState(State state);

    void setVotedFor(Integer votedFor);

    Integer getVotedFor();

    Integer getVoteTime();

    String getName();
}
