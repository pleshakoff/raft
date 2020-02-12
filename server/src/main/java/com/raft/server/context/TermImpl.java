package com.raft.server.context;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;


@Component
@Slf4j
public class TermImpl implements Term {


    @Value("${raft.id}")
    Integer id;

    private final AtomicLong currentTerm = new AtomicLong(0L);


    @Override
    public Long getCurrentTerm() {
        return currentTerm.get();
    }

    @Override
    public void setCurrentTerm(long currentTerm) {
        this.currentTerm.set(currentTerm);
        log.info("Peer #{} Set term to {}", id,getCurrentTerm());
    }


    @Override
    public Long incCurrentTerm() {
        currentTerm.incrementAndGet();
        log.info("Peer #{} Term incremented: {}",id, getCurrentTerm());
        return getCurrentTerm();
    }
}
