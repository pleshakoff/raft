package com.raft.server.node.peers;

public interface Peers {
    Peer get(Integer id);

    java.util.List<Peer> getPeers();

    Integer getQuorum();
}
