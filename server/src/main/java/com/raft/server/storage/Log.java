package com.raft.server.storage;

public interface Log {

    void append(Operation operation);
    void remove(Long index);

}
