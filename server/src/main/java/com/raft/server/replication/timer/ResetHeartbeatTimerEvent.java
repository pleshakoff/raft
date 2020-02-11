package com.raft.server.replication.timer;

import org.springframework.context.ApplicationEvent;

public class ResetHeartbeatTimerEvent extends ApplicationEvent {

    public ResetHeartbeatTimerEvent(Object source) {
        super(source);
    }
}
