package com.raft.server.storage;


import com.raft.server.context.ContextDecorator;
import com.raft.server.context.State;
import com.raft.server.exceptions.NotLeaderException;
import com.raft.server.log.Operation;
import com.raft.server.log.OperationType;
import com.raft.server.log.OperationsLog;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.raft.server.log.OperationType.*;

@Service
@RequiredArgsConstructor
class StorageServiceImpl implements StorageService {

    private final Storage storage;
    private final ContextDecorator context;

    @Override
    public String get(Long id) {
        context.cancelIfNotActive();
        return storage.get(id);
    }

    @Override
    public List<Entry> all() {
        context.cancelIfNotActive();
        return storage.all();
    }



}
