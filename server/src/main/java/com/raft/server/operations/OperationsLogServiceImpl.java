package com.raft.server.operations;


import com.raft.server.node.Attributes;
import com.raft.server.node.State;
import com.raft.server.node.term.Term;
import com.raft.server.exceptions.NotLeaderException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.raft.server.operations.OperationType.*;

@Service
@RequiredArgsConstructor
@Slf4j
class OperationsLogServiceImpl implements OperationsLogService {

    private final OperationsLog operationsLog;
    private final Attributes attributes;
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
        log.info("Peer #{} SNEAKY INSERT!!! key:{} val:{}", attributes.getId(),entry.getKey(),entry.getVal());
        Operation operation = new Operation(term.getCurrentTerm(), INSERT, entry);
        operationsLog.append(operation);
    }

    @Override
    public List<Operation> all() {
        return operationsLog.all();
    }

    private void appendToLog(OperationType type, Entry entry) {
        log.info("Peer #{} Append new operation. {}. key:{} val:{}", attributes.getId(),type,entry.getKey(),entry.getVal());
        attributes.cancelIfNotActive();
        if (!attributes.getState().equals(State.LEADER)) {
            throw new NotLeaderException();
        }
        Operation operation = new Operation(term.getCurrentTerm(), type, entry);
        operationsLog.append(operation);
        applicationEventPublisher.publishEvent(new OperationsLogAppendedEvent(this));
    }
}
