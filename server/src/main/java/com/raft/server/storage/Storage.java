package com.raft.server.storage;

import java.util.List;

public interface Storage {
    List<Entry> all();
    String get(Long key);
}
