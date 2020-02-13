package com.raft.server.node.peers;

public interface Peer {

    Integer getId();

    Integer getNextIndex();

    void setNextIndex(Integer nextIndex);

    void decNextIndex();

    Integer getMatchIndex();

    Boolean getVoteGranted();

    void setMatchIndex(Integer matchIndex);

    void setVoteGranted(Boolean voteGranted);

}
