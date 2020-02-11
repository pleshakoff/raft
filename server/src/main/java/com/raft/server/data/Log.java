package com.raft.server.data;

public interface Log {

    void append(Operation operation);
    Operation get(Integer index);
}
