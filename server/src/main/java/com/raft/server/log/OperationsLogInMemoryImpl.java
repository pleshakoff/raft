package com.raft.server.log;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
class OperationsLogInMemoryImpl implements OperationsLog {

    private List<Operation> operationsLog = new ArrayList<>();

    @Override
    public void append(Operation operation) {
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
        if (lastIndex > -1) {
            return operationsLog.get(lastIndex).getTerm();
        }
        else
          return 0L;
    }

    @Override
    public void removeAllFromIndex(int index) {
        log.info("Remove operations from log. From index {} to {}",index,operationsLog.size()-1);

        int delta = operationsLog.size()-index;
        for (int i = 0; i < delta; i++) {
            operationsLog.remove(index);
        }
    }

    @Override
    public Long getTerm(Integer index) {
       return operationsLog.get(index).getTerm();
    }


}
