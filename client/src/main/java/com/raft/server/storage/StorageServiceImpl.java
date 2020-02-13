package com.raft.server.storage;

import com.network.http.Http;
import com.raft.server.exchange.ExchangeService;
import com.raft.server.operations.Entry;
import com.raft.server.operations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class StorageServiceImpl implements StorageService {

    private final Http http;
    private final ExchangeService exchangeService;

    @Override
    public String get(Long key,
                      Integer peerId) {
        exchangeService.checkAvailable(peerId);
        return http.callGet(peerId.toString(), String.class, "storage", key.toString()).getBody();
    }

    @Override
    public List<Entry> all(Integer peerId) {
        exchangeService.checkAvailable(peerId);
        return http.callGet(peerId.toString(), new ParameterizedTypeReference<List<Entry>>() {}, "storage").getBody();
    }


    @Override
    public String insert(Entry entry) {
        Integer leaderId = exchangeService.getLeaderId();
        return http.callPost(leaderId.toString(),String.class,entry,"log").getBody();
    }

    @Override
    public String update(Long key,
                         String val) {
        Integer leaderId = exchangeService.getLeaderId();
        return http.callPut(leaderId.toString(), String.class, val,"log",key.toString()).getBody();
    }

    @Override
    public String delete(Long key) {
        Integer leaderId = exchangeService.getLeaderId();
        return http.callDelete(leaderId.toString(),"log",key.toString()).getBody();
    }
}
