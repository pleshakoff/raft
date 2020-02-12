package com.raft.server.context.peers;

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
