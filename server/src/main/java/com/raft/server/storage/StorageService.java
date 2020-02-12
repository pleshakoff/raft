package com.raft.server.storage;

import com.raft.server.log.Operation;

import java.util.List;

public interface StorageService {

    String get(Long id);

    List<Entry> all();


}
