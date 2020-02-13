package com.raft.server.storage;


import com.raft.server.node.Attributes;
import com.raft.server.operations.Entry;
import com.raft.server.operations.Operation;
import com.raft.server.operations.OperationsLog;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
class StorageServiceImpl implements StorageService {

    private final Storage storage;
    private final Attributes attributes;
    private final OperationsLog operationsLog;

    @Override
    public String get(Long id) {
        attributes.cancelIfNotActive();
        return storage.get(id);
    }

    @Override
    public List<Entry> all() {
        attributes.cancelIfNotActive();
        return storage.all();
    }

    @Override
    public void applyCommitted(){
        log.debug("Peer #{} Trying to apply committed.LastApplied: {}, Commit Index: {} ", attributes.getId(), attributes.getLastApplied(),
                  attributes.getCommitIndex());
        while (attributes.getLastApplied()< attributes.getCommitIndex())
        {
            apply(attributes.getCommitIndex());
            attributes.incLastApplied();
        }
    }

    private void apply(Integer index){
        log.info("Peer #{} Apply commit index: {}", attributes.getId(), index);
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
