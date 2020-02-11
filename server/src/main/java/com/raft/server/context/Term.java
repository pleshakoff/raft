package com.raft.server.context;

public interface Term {

    Long getCurrentTerm();

    void setCurrentTerm(long currentTerm);

    Long incCurrentTerm();

}
