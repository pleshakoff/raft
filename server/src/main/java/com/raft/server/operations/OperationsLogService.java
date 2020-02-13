package com.raft.server.operations;

import java.util.List;

interface OperationsLogService {


    List<Operation> all();

    void insert(Entry entry);

    void update(Long key, String val);

    void delete(Long key);

    void sneakyInsert(Entry entry);
}
