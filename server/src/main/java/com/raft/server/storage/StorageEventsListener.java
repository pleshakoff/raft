package com.raft.server.storage;


import com.raft.server.node.CommittedIndexChangedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StorageEventsListener implements ApplicationListener<CommittedIndexChangedEvent> {

    private final StorageService storageService;

    @Override
    public void onApplicationEvent(CommittedIndexChangedEvent event) {
        storageService.applyCommitted();
    }
}
