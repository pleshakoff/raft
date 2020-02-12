package com.raft.server.context.peers;

public interface Peers {

    Peer get(Integer id);

    java.util.List<Peer> getPeers();

    Integer getQuorum();
}
