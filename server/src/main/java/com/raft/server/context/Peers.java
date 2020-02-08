package com.raft.server.context;


import com.network.Service;
import com.network.ServicesProps;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
class Peers {

    @Value("${raft.id}")
    Integer id;

    private final ServicesProps servicesProps;

    @Getter(AccessLevel.PACKAGE)
    private Integer quorum;

    @Getter
    private List<Peer> peers = new ArrayList<>();

    private void add(Integer id) {
        peers.add(new Peer(id));
    }


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
                filter(id -> !id.equals(this.id)).
                forEach(this::add);

        quorum = servicesProps.getServices().size()/2+1;

    }
}
