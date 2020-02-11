package com.raft.server.context;

public interface Peers {

    Peer get(Integer id);

    java.util.List<Peer> getPeers();

    Integer getQuorum();
}
