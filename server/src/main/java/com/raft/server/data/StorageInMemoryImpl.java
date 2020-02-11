package com.raft.server.data;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Component
class StorageInMemoryImpl implements Storage {

    private Map<Long,String> map = new HashMap<>();

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


    @Override
    public void insert(Long key, String val) {
        map.put(key,val);
    }

    @Override
    public void update(Long key, String val) {
        map.put(key,val);
    }

    @Override
    public void delete(Long key)
    {
        map.remove(key);
    }
}
