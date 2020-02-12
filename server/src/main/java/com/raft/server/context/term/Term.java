package com.raft.server.context.term;

public interface Term {

    Long getCurrentTerm();

    void setCurrentTerm(long currentTerm);

    Long incCurrentTerm();

}
