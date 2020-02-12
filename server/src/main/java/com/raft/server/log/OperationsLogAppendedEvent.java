package com.raft.server.log;

import org.springframework.context.ApplicationEvent;

public class OperationsLogAppendedEvent extends ApplicationEvent {

    OperationsLogAppendedEvent(Object source) {
        super(source);
    }
}
