package com.raft.server.context;

import com.network.http.Http;
import com.raft.server.exceptions.LeaderNotFoundException;
import com.raft.server.exchange.ExchangeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
class ContextServiceImpl implements ContextService {


    private final ExchangeService exchangeService;
    private final Http http;


    @Override
    public void start(Integer peerId) {
        exchangeService.checkAvailable(peerId);
        http.callPost(peerId.toString(),null,null,"context","start");
    }

    @Override
    public void stop(Integer peerId) {
        exchangeService.checkAvailable(peerId);
        http.callPost(peerId.toString(),null,null,"context","stop");
    }

    @Override
    public List<Context> all() {
        return exchangeService.requestContextFromAllPeers();
    }

    @Override
    public Integer leader() {
        return exchangeService.getLeaderId();
    }
}
