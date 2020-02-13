package com.raft.server.storage;

import com.raft.server.operations.Entry;

import java.util.List;

public interface StorageService {

    String get(Long id);

    List<Entry> all();


    void applyCommitted();
}
