package com.raft.server.node;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Timer;
import java.util.TimerTask;

@Service
@RequiredArgsConstructor
@Slf4j
public class VoteTimer {

    private final NodeState nodeState;
    private final Timer timer = new Timer();

    @Getter
    private volatile int counter = 0;

    public int getTimeToNextVote(){
        return nodeState.getVoteTime()-counter;
    }

    @PostConstruct
    public void start() {

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
              counter++;
              log.info("{} Time to next vote {}", nodeState.getName(),nodeState.getVoteTime()-counter);
              if (counter>=nodeState.getVoteTime())
              {
                  log.info("START VOTE");
                  counter=0;
              }


            }
        }, 0, 1000);

    }






}
