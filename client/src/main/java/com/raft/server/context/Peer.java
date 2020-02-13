package com.raft.server.context;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@RequiredArgsConstructor
@Getter
public class Peer {

    private final Integer nextIndex;
    private final Integer matchIndex;
    private final Boolean voteGranted;

}
