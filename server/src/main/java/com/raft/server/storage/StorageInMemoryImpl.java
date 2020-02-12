package com.raft.server.storage;

import com.raft.server.log.Entry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


@Component
@Slf4j
@RequiredArgsConstructor
class StorageInMemoryImpl implements Storage {

     private final Map<Long,String> map = new ConcurrentHashMap<>();


    @Override
    public String get(Long key) {
        return map.get(key);
    }

    @Override
    public void insert(Long key,
                       String val) {
        map.put(key,val);
    }

    @Override
    public void update(Long key,
                       String val) {
        map.put(key,val);
    }

    @Override
    public void delete(Long key) {
        map.remove(key);
    }

    @Override
    public List<Entry> all() {
        return map.entrySet().stream().
               map(entry -> new Entry(entry.getKey(),entry.getValue())).
                collect(Collectors.toList());
    }

}
