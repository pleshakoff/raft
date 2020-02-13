package com.raft.server.operations;

import org.springframework.context.ApplicationEvent;

public class OperationsLogAppendedEvent extends ApplicationEvent {

    OperationsLogAppendedEvent(Object source) {
        super(source);
    }
}
