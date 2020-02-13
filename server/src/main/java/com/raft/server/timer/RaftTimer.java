package com.raft.server.timer;


import com.raft.server.node.Attributes;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;


@Slf4j
public abstract class RaftTimer {

    protected final Attributes attributes;
    private final Timer timer = new Timer();

    abstract protected Integer getTimeout();
    abstract protected String getActionName();
    abstract protected Runnable getAction();
    abstract protected boolean isRun();

    @Getter
    private final AtomicInteger counter = new AtomicInteger(0);

    protected RaftTimer(Attributes attributes) {
        this.attributes = attributes;
    }



    public void reset() {
        counter.set(0);
    }


    @PostConstruct
    private void start() {

        timer.schedule(new TimerTask() {
            @Override

            public void run() {
                if (isRun()) {
                    counter.incrementAndGet();
                    log.debug("Peer #{} Time to next {}: {} sec", attributes.getId(), getActionName(), getTimeout() - counter.get());
                    if (counter.get() >= getTimeout()) {
                        counter.set(0);
                        getAction().run();
                    }
                }else
                    counter.set(0);
            }
        }, 0, 1000);

    }


}
