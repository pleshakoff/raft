package com.raft.server.node;

public interface Attributes {
    State getState();

    void setState(State state);

    Integer getCommitIndex();

    void setCommitIndex(Integer commitIndex);

    Integer getLastApplied();

    void incLastApplied();

    void setActive(Boolean active);

    void setVotedFor(Integer votedFor);

    Integer getId();

    Boolean getActive();

    Integer getVotedFor();

    Integer getElectionTimeout();

    Integer getHeartBeatTimeout();

    void cancelIfNotActive();
}
