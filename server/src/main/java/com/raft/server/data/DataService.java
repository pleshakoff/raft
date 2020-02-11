package com.raft.server.data;

import java.util.List;

public interface DataService {

    String get(Long id);

    List<Entry> all();

    void insert(Entry entry);

    void update(Long key, String val);

    void delete(Long key);
}
