package com.raft.server.replication.timer;


import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class HeartbeatTimerListener implements ApplicationListener<ResetHeartbeatTimerEvent> {

    private  final HeartBeatTimer electionTimer;


    @Override
    public void onApplicationEvent(ResetHeartbeatTimerEvent event) {
        electionTimer.reset();
    }
}
