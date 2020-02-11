package com.raft.server.data;


import com.raft.server.context.Context;
import com.raft.server.context.State;
import com.raft.server.exceptions.NotLeaderException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.raft.server.data.OperationType.*;

@Service
@RequiredArgsConstructor
class DataServiceImpl implements DataService {

    private final Storage storage;
    private final Log log;
    private final Context context;

    @Override
    public String get(Long id) {
        context.cancelIfNotActive();
        return storage.get(id);
    }

    @Override
    public  List<Entry> all() {
        context.cancelIfNotActive();
        return storage.all();
    }


    @Override
    public void insert(Entry entry) {
        appendToLog(INSERT, entry);
    }

    @Override
    public void update(Long key, String val) {
        appendToLog(UPDATE, new Entry(key,val));
    }

    @Override
    public void delete(Long key) {
        appendToLog(DELETE, new Entry(key,null));
    }

    private void appendToLog(OperationType insert, Entry entry) {
        context.cancelIfNotActive();
        if (!context.getState().equals(State.LEADER)) {
            throw new NotLeaderException();
        }
        Operation operation = new Operation(context.getCurrentTerm(), insert, entry);
        log.append(operation);
    }
}
