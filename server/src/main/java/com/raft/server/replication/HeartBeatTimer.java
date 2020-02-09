package com.raft.server.replication;


import com.raft.server.context.Context;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class HeartBeatTimer {

    private final ReplicationService replicationService;
    private final Timer timer = new Timer();


    @PostConstruct
    public void start() {

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                    replicationService.sendAppendEntries();
            }
        }, 0, 2000);

    }





}
