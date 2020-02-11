package com.raft.server.data;

public interface OperationsLog {

    void append(Operation operation);
    Operation get(Integer index);
    Long getTerm(Integer index);
    Integer getLastIndex();

    Long getLastTerm();


    void removeAllFromIndex(int newOperationIndex);
}
