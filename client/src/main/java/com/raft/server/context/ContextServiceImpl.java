package com.raft.server.context;

import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ContextServiceImpl implements ContextService {
    @Override
    public void stop(Integer peerId) {

    }

    @Override
    public void start(Integer peerId) {

    }

    @Override
    public List<Context> all() {
        return null;
    }

    @Override
    public Integer leader() {
        return null;
    }
}
