package com.raft.server.node.term;

public interface Term {

    Long getCurrentTerm();

    void setCurrentTerm(long currentTerm);

    Long incCurrentTerm();

}
