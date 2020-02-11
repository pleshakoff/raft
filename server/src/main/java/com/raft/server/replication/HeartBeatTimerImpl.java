package com.raft.server.replication;


import com.raft.server.context.Context;
import com.raft.server.timer.RaftTimer;
import org.springframework.stereotype.Service;

import java.util.Timer;
import java.util.concurrent.atomic.AtomicInteger;

import static com.raft.server.context.State.LEADER;

@Service
class HeartBeatTimerImpl extends RaftTimer implements HeartBeatTimer  {


    private final ReplicationService replicationService;

    protected HeartBeatTimerImpl(Context context, ReplicationService replicationService) {
        super(context);
        this.replicationService = replicationService;
    }

    @Override
    protected Integer getTimeout() {
        return context.getHeartBeatTimeout();
    }

    @Override
    protected String getActionName() {
        return "heart beat";
    }

    @Override
    protected Runnable getAction() {
        return replicationService::heartBeat;
    }

    @Override
    protected boolean isRun() {
            return context.getActive() && context.getState().equals(LEADER);
    }


}
