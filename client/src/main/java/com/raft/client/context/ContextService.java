package com.raft.client.context;

import java.util.List;

public interface ContextService {
    void stop(Integer peerId);

    void start(Integer peerId);

    List<Context> all();

    Integer leader();
}
