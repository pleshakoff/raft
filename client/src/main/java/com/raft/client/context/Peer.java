package com.raft.client.context;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class Peer {

    private final Integer nextIndex;
    private final Integer matchIndex;
    private final Boolean voteGranted;

}
