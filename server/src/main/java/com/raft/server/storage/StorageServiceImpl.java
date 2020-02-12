package com.raft.server.storage;


import com.raft.server.context.Context;
import com.raft.server.log.Entry;
import com.raft.server.log.Operation;
import com.raft.server.log.OperationsLog;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.raft.server.log.OperationType.INSERT;

@Service
@Slf4j
@RequiredArgsConstructor
class StorageServiceImpl implements StorageService {

    private final Storage storage;
    private final Context context;
    private final OperationsLog operationsLog;

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

    @Override
    public void applyCommitted(){

        while (context.getLastApplied()<context.getCommitIndex())
        {
            apply(context.getCommitIndex());
            context.incLastApplied();
        }
    }

    private void apply(Integer index){
        log.info("Peer #{} Apply commit index: {}", context.getId(), index);
        Operation operation = operationsLog.get(index);
        Entry entry = operation.getEntry();

        switch (operation.getType()) {
            case INSERT:
                storage.insert(entry.getKey(),entry.getVal());
            break;
            case UPDATE:
                storage.update(entry.getKey(),entry.getVal());
                break;
            case DELETE:
                storage.delete(entry.getKey());
                break;
            default:
                throw  new RuntimeException("Unsupported operation");
        }


    }




}
