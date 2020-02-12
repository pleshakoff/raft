package com.raft.server.log;

import java.util.List;

public interface OperationsLog {

    void append(Operation operation);
    Operation get(Integer index);

    List<Operation> all();

    Long getTerm(Integer index);
    Integer getLastIndex();
    Long getLastTerm();
    Boolean isEmpty();

    void removeAllFromIndex(int newOperationIndex);
}
