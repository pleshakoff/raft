package com.raft.server.election.timer;


import com.raft.server.node.Attributes;
import com.raft.server.election.ElectionService;
import com.raft.server.timer.RaftTimer;
import org.springframework.stereotype.Service;

import static com.raft.server.node.State.LEADER;

@Service
class ElectionTimerImpl extends RaftTimer implements ElectionTimer {

    private final ElectionService electionService;

    protected ElectionTimerImpl(Attributes attributes, ElectionService electionService) {
        super(attributes);
        this.electionService = electionService;
    }

    @Override
    protected Integer getTimeout() {
        return attributes.getElectionTimeout();
    }

    @Override
    protected String getActionName() {
        return "vote";
    }


    @Override
    protected Runnable getAction() {
        return electionService::processElection;
    }

    @Override
    protected boolean isRun() {
        return attributes.getActive() && !attributes.getState().equals(LEADER);
    }

}
