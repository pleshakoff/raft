package com.raft.server.operations;

import com.raft.server.node.Attributes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
class OperationsLogInMemoryImpl implements OperationsLog {


    private static final Integer EMPTY_LOG_LAST_INDEX = -1;
    private final Attributes attributes;


    private final List<Operation> operationsLog = new ArrayList<>();

    @Override
    synchronized public void  append(Operation operation) {
       operationsLog.add(operation);
    }

    @Override
    public Operation get(Integer index) {
        return operationsLog.get(index);
    }

    @Override
    public List<Operation> all() {
        return operationsLog;
    }

    @Override
    public Integer getLastIndex() {
        return operationsLog.size()-1;

    }

    @Override
    public Long getLastTerm() {

        Integer lastIndex = getLastIndex();
        if (lastIndex > EMPTY_LOG_LAST_INDEX) {
            return operationsLog.get(lastIndex).getTerm();
        }
        else
          return 0L;
    }


    @Override
    synchronized public void removeAllFromIndex(int index) {
        log.info("Peer #{} Remove operations from operations. From index {} to {}",attributes.getId(),index,operationsLog.size()-1);

        int delta = operationsLog.size()-index;
        for (int i = 0; i < delta; i++) {
            operationsLog.remove(index);
        }
    }

    @Override
    public Long getTerm(Integer index) {
        if (index > EMPTY_LOG_LAST_INDEX) {
            return operationsLog.get(index).getTerm();
        }
        else
            return 0L;
    }


}
