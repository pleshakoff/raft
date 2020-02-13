package com.raft.client.storage;

import com.raft.client.operations.Entry;

import java.util.List;

public interface StorageService {


    String get(Long key,
               Integer peerId);

    List<Entry> all(Integer peerId);

    String insert(Entry entry);

    String update(Long key,String val);

    String delete(Long key);


}
