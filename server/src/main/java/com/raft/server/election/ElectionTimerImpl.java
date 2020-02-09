package com.raft.server.election;


import com.raft.server.context.Context;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

import static com.raft.server.context.State.*;

@Service
@RequiredArgsConstructor
@Slf4j
class ElectionTimerImpl implements ElectionTimer {

    private final Context context;
    private final ElectionService electionService;
    private final Timer timer = new Timer();

    @Getter
    private AtomicInteger counter = new AtomicInteger(0);

    public int getTimeToNextVote() {
        return context.getElectionTimeout() - counter.get();
    }

    @Override
    public void reset() {
        counter.set(0);
    }

    ;


    @PostConstruct
    public void start() {

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (context.getActive() && !context.getState().equals(LEADER)) {

                    counter.incrementAndGet();
                    log.info("Peer #{}. Time to next vote: {} sec", context.getId(), context.getElectionTimeout() - counter.get());
                    if (counter.get() >= context.getElectionTimeout()) {
                        counter.set(0);
                        electionService.processElection();
                    }
                }else
                    counter.set(0);
            }
        }, 0, 1000);

    }


}
