package com.raft.server.vote;


import com.raft.server.node.Context;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

import static com.raft.server.node.State.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class VoteTimer {

    private final Context context;
    private final Timer timer = new Timer();

    @Getter
    private AtomicInteger counter = new AtomicInteger(0);

    public int getTimeToNextVote(){
        return context.getElectionTimeout()-counter.get();
    }

    public void reset(){
        counter.set(0);
    };


    @PostConstruct
    public void start() {

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
              counter.incrementAndGet();
            //  log.info("{} Time to next vote {}", context.getId(),context.getElectionTimeout()-counter.get());
              if (counter.get()>= context.getElectionTimeout()&& !context.getState().equals(LEADER))
              {
                  log.info("Peer #{} START VOTE", context.getId());
                  context.setState(CANDIDATE);
                  counter.set(0);
              }
            }
        }, 0, 1000);

    }






}
