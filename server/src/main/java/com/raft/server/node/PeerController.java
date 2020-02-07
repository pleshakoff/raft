package com.raft.server.node;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.util.List;


@RestController
@RequestMapping(value = "/node",produces = {MediaType.APPLICATION_JSON_VALUE})
@Api(tags="Peer")
@RequiredArgsConstructor
public class PeerController {

    private final Context context;
    private final Peers peers;

    @GetMapping
    @ApiOperation(value = "Get current peer state")
    public Context getCurrentPeerState()  {
      return context;
    }

    @GetMapping("/peers")
    @ApiOperation(value = "Get list of others peers")
    public List<Peer> getMyGroups()  {
        return peers.getPeers();
    }


    @PostMapping("/stop")
    @ApiOperation(value = "Stop")
    public void stop()  {
        context.setActive(false);
    }

    @PostMapping("/start")
    @ApiOperation(value = "Start")
    public void start()  {
        context.setActive(true);
    }


}
