package com.raft.server.node.term;

import com.raft.server.node.Attributes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;


@Component
@Slf4j
@RequiredArgsConstructor
public class TermImpl implements Term {



    private final AtomicLong currentTerm = new AtomicLong(0L);
    private final Attributes attributes;


    @Override
    public Long getCurrentTerm() {
        return currentTerm.get();
    }

    @Override
    public void setCurrentTerm(long currentTerm) {
        this.currentTerm.set(currentTerm);
        log.info("Peer #{} Set term to {}", attributes.getId(),getCurrentTerm());
    }


    @Override
    public Long incCurrentTerm() {
        currentTerm.incrementAndGet();
        log.info("Peer #{} Term incremented: {}",attributes.getId(), getCurrentTerm());
        return getCurrentTerm();
    }
}
