package com.raft.server.log;

import com.raft.server.storage.Entry;

import java.util.List;

interface LogService {


    void insert(Entry entry);

    void update(Long key, String val);

    void delete(Long key);

    void sneakyInsert(Entry entry);

    List<Operation> all();
}
