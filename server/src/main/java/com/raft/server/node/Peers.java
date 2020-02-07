package com.raft.server.node;


import com.network.Service;
import com.network.ServicesProps;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class Peers {

    private final Context nodeState;
    private final ServicesProps servicesProps;

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
                filter(id -> !id.equals(nodeState.getId())).
                forEach(this::add);

    }
}
