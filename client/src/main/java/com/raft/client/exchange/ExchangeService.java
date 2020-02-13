package com.raft.client.exchange;

import com.raft.client.context.Context;

import java.util.List;

public interface ExchangeService {
    List<Context> requestContextFromAllPeers();

    Integer getLeaderId();

    void checkAvailable(Integer peerId);
}
