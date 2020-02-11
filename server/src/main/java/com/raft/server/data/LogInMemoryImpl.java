package com.raft.server.data;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LogInMemoryImpl implements Log {

    private List<Operation> log;

    @Override
    public void append(Operation operation) {
       log.add(operation);
    }

    @Override
    public Operation get(Integer index) {
        return log.get(index);
    }


}
