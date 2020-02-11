package com.raft.server.election;


import com.raft.server.context.Context;
import com.raft.server.timer.RaftTimer;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.TimerTask;

import static com.raft.server.context.State.*;

@Service
class ElectionTimerImpl extends RaftTimer implements ElectionTimer {

    private final ElectionService electionService;

    protected ElectionTimerImpl(Context context, ElectionService electionService) {
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
