package com.raft.server.replication;


import com.raft.server.operations.OperationsLogAppendedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class ReplicationEventsListener implements ApplicationListener<OperationsLogAppendedEvent> {

    private  final ReplicationService replicationService;


    @Override
    public void onApplicationEvent(OperationsLogAppendedEvent event) {
        replicationService.appendRequest();
    }
}
