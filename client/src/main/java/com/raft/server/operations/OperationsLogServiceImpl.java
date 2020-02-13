package com.raft.server.operations;

import com.network.http.Http;
import com.raft.server.exchange.ExchangeService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class OperationsLogServiceImpl implements OperationsLogService {

    private final Http http;
    private final ExchangeService exchangeService;

    @Override
    public List<Operation> all(Integer peerId) {
        exchangeService.checkAvailable(peerId);
        return http.callGet(peerId.toString(), new ParameterizedTypeReference<List<Operation>>() {}, "log").getBody();
    }



}
