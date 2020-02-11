package com.raft.server.context;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;


@Getter
@RequiredArgsConstructor
public class Peer {

    final Integer id;

    @Setter
    Integer nextIndex=1;

    @Setter
    Integer matchIndex=0;

    @Setter
    Boolean voteGranted=false;

}
