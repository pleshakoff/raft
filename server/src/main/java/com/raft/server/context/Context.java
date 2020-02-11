package com.raft.server.context;

import java.util.List;



public interface Context {

    Integer getId();


    void setActive(Boolean active);

    Boolean getActive();

    void cancelIfNotActive();


    State getState();

    void setState(State state);


    Integer getQuorum();

    Integer getVotedFor();

    void setVotedFor(Integer votedFor);


    Long getCurrentTerm();

    void setCurrentTerm(long currentTerm);

    boolean checkTermGreaterThenCurrent(Long term);

    Long incCurrentTerm();


    Long getCommitIndex();

    void incCommitIndex();

    Long getLastApplied();

    void incLastApplied();

    Integer getElectionTimeout();
    Integer getHeartBeatTimeout();

    List<Peer> getPeers();

}
