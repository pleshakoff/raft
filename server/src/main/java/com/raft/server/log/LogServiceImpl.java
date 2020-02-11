package com.raft.server.log;


import com.raft.server.context.ContextDecorator;
import com.raft.server.context.State;
import com.raft.server.exceptions.NotLeaderException;
import com.raft.server.replication.ReplicationService;
import com.raft.server.storage.Entry;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.raft.server.log.OperationType.*;

@Service
@RequiredArgsConstructor
class LogServiceImpl implements LogService {

    private final OperationsLog operationsLog;
    private final ContextDecorator context;
    private final ReplicationService replicationService;

    @Override
    public void insert(Entry entry) {
        appendToLog(INSERT, entry);
    }

    @Override
    public void update(Long key, String val) {
        appendToLog(UPDATE, new Entry(key, val));
    }

    @Override
    public void delete(Long key) {
        appendToLog(DELETE, new Entry(key, null));
    }

    @Override
    public void sneakyInsert(Entry entry) {
        Operation operation = new Operation(context.getCurrentTerm(), INSERT, entry);
        operationsLog.append(operation);
    }

    @Override
    public List<Operation> all() {
        return operationsLog.all();
    }

    private void appendToLog(OperationType insert, Entry entry) {
        context.cancelIfNotActive();
        if (!context.getState().equals(State.LEADER)) {
            throw new NotLeaderException();
        }
        Operation operation = new Operation(context.getCurrentTerm(), insert, entry);
        operationsLog.append(operation);
        replicationService.appendRequest();

    }
}
