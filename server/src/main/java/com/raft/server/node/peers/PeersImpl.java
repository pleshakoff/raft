package com.raft.server.node.peers;


import com.network.Service;
import com.network.ServicesProps;
import com.raft.server.node.Attributes;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
class PeersImpl implements Peers {


    private final ServicesProps servicesProps;
    private final Attributes attributes;

    @Getter
    private Integer quorum;

    @Getter
    private final List<Peer> peers = new ArrayList<>();

    private void add(Integer id) {
        peers.add(new PeerImpl(id));
    }


    @Override
    public  Peer get(Integer id) {
        return peers.stream().
                filter(peer -> peer.getId().equals(id)).
                findFirst().
                orElseThrow(() -> new RuntimeException(String.format("Unsupported peer Id %s",id)));
    }

    @PostConstruct
     void init() {
        servicesProps.getServices().stream().
                map(Service::getName).
                map(Integer::parseInt).
                filter(id -> !id.equals(attributes.getId())).
                forEach(this::add);

        quorum = servicesProps.getServices().size()/2+1;

    }
}
