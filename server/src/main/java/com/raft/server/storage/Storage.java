package com.raft.server.storage;

import com.raft.server.operations.Entry;

import java.util.List;

public interface Storage {

    List<Entry> all();

    String get(Long key);

    void insert(Long key, String val);

    void update(Long key, String val);

    void delete(Long key);


}
