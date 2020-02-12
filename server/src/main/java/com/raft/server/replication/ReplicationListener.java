package com.raft.server.replication;


import com.raft.server.replication.timer.ResetHeartbeatTimerEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class ReplicationListener implements ApplicationListener<ResetHeartbeatTimerEvent> {

    private  final ReplicationService replicationService;


    @Override
    public void onApplicationEvent(ResetHeartbeatTimerEvent event) {
        replicationService.appendRequest();
    }
}
