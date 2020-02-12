package com.raft.server.storage;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


@Component
class StorageInMemoryImpl implements Storage {

    private Map<Long,String> map = new ConcurrentHashMap<>();

    @Override
    public String get(Long key) {
        return map.get(key);
    }

    @Override
    public List<Entry> all() {
        return map.entrySet().stream().
               map(entry -> new Entry(entry.getKey(),entry.getValue())).
                collect(Collectors.toList());
    }

}
