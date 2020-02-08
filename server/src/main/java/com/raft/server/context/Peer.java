package com.raft.server.context;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;


@Getter
@RequiredArgsConstructor
class Peer {

    final Integer id;
    @Setter
    Long nextIndex=0L;
    @Setter
    Long matchIndex=0L;
    @Setter
    Boolean voteGranted=false;

}
