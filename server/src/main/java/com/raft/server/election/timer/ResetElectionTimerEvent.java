package com.raft.server.election.timer;

import org.omg.CORBA.portable.ApplicationException;
import org.springframework.context.ApplicationEvent;

public class ResetElectionTimerEvent extends ApplicationEvent {

    public ResetElectionTimerEvent(Object source) {
        super(source);
    }
}
