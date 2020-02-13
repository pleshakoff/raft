package com.raft.server.exchange;

import com.raft.server.context.Context;

import java.util.List;

public interface ExchangeService {
    List<Context> requestContextFromAllPeers();

    Integer getLeaderId();

    void checkAvailable(Integer peerId);
}
