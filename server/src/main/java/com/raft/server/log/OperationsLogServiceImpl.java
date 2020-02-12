package com.raft.server.log;


import com.raft.server.context.Context;
import com.raft.server.context.State;
import com.raft.server.context.term.Term;
import com.raft.server.exceptions.NotLeaderException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.raft.server.log.OperationType.*;

@Service
@RequiredArgsConstructor
class OperationsLogServiceImpl implements OperationsLogService {

    private final OperationsLog operationsLog;
    private final Context context;
    private final Term term;
    private final ApplicationEventPublisher applicationEventPublisher;


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
        Operation operation = new Operation(term.getCurrentTerm(), INSERT, entry);
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
        Operation operation = new Operation(term.getCurrentTerm(), insert, entry);
        operationsLog.append(operation);
        applicationEventPublisher.publishEvent(new OperationsLogAppendedEvent(this));
    }
}
