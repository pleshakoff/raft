package com.raft.server.replication.timer;


import com.raft.server.node.Attributes;
import com.raft.server.replication.ReplicationService;
import com.raft.server.timer.RaftTimer;
import org.springframework.stereotype.Service;

import static com.raft.server.node.State.LEADER;

@Service
class HeartBeatTimerImpl extends RaftTimer  {


    private final ReplicationService replicationService;

    protected HeartBeatTimerImpl(Attributes attributes, ReplicationService replicationService) {
        super(attributes);
        this.replicationService = replicationService;
    }

    @Override
    protected Integer getTimeout() {
        return attributes.getHeartBeatTimeout();
    }

    @Override
    protected String getActionName() {
        return "heart beat";
    }

    @Override
    protected Runnable getAction() {
        return replicationService::appendRequest;
    }

    @Override
    protected boolean isRun() {
            return attributes.getActive() && attributes.getState().equals(LEADER);
    }


}
