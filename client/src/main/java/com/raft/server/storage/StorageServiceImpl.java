package com.raft.server.storage;

import com.raft.server.operations.Entry;
import org.springframework.stereotype.Service;

import java.util.List;



@Service
public class StorageServiceImpl implements StorageService {
    @Override
    public String get(Long key,
                      Integer peerId) {
        return null;
    }

    @Override
    public List<Entry> all(Integer peerId) {
        return null;
    }

    @Override
    public String insert(Entry entry) {
        return null;
    }

    @Override
    public String update(Long key,
                         String val) {
        return null;
    }

    @Override
    public String delete(Long key) {
        return null;
    }
}
