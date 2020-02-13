package com.raft.server.context;

import com.raft.server.node.State;
import com.raft.server.node.peers.Peer;


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

    Integer getCommitIndex();

    void setCommitIndex(Integer commitIndex);

    Integer getLastApplied();

    void incLastApplied();

    Integer getElectionTimeout();
    Integer getHeartBeatTimeout();

    List<Peer> getPeers();
    Peer getPeer(Integer id);

    Long incCurrentTerm();

    void setCurrentTerm(Long term);

    void setTermGreaterThenCurrent(Long term);

    Integer getLastIndexSize();
}
