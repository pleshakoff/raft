package com.raft.server.election.timer;


import com.raft.server.context.ContextDecorator;
import com.raft.server.election.ElectionService;
import com.raft.server.election.timer.ElectionTimer;
import com.raft.server.timer.RaftTimer;
import org.springframework.stereotype.Service;

import static com.raft.server.context.State.*;

@Service
class ElectionTimerImpl extends RaftTimer implements ElectionTimer {

    private final ElectionService electionService;

    protected ElectionTimerImpl(ContextDecorator context, ElectionService electionService) {
        super(context);
        this.electionService = electionService;
    }

    @Override
    protected Integer getTimeout() {
        return context.getElectionTimeout();
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
        return context.getActive() && !context.getState().equals(LEADER);
    }

}
