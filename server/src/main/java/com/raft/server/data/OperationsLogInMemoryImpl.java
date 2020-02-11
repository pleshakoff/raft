package com.raft.server.data;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class OperationsLogInMemoryImpl implements OperationsLog {

    private List<Operation> operationsLog;

    @Override
    public void append(Operation operation) {
       operationsLog.add(operation);
    }

    @Override
    public Operation get(Integer index) {
        return operationsLog.get(index);
    }

    @Override
    public Integer getLastIndex() {
        return operationsLog.size()-2;

    }

    @Override
    public Long getLastTerm() {
        return operationsLog.get(getLastIndex()).getTerm();
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
