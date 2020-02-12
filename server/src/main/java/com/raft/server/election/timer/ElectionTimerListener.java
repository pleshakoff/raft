package com.raft.server.election.timer;


import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ElectionTimerListener implements ApplicationListener<ResetElectionTimerEvent> {

    private  final  ElectionTimer electionTimer;


    @Override
    public void onApplicationEvent(ResetElectionTimerEvent event) {
        electionTimer.reset();
    }
}
