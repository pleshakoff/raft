package com.raft.server.storage;

import com.raft.server.log.Operation;

import java.util.List;

public interface StorageService {

    String get(Long id);

    List<Entry> all();

    void insert(Entry entry);

    void update(Long key, String val);

    void delete(Long key);

    void sneakyInsert(Entry entry);

    List<Operation> getLog();
}
