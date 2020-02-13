package com.raft.server.context;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping(value = "/context", produces = {MediaType.APPLICATION_JSON_VALUE})
@Api(tags = "Attributes")
@RequiredArgsConstructor
class ContextController {

    private final ContextService contextService;

    @GetMapping
    @ApiOperation(value = "Get all peers data")
    public List<Context> getCurrentPeerState() {
        return contextService.all();
    }

    @GetMapping("/leader")
    @ApiOperation(value = "Get isLeader ID")
    public Integer getLeader() {
        return contextService.leader();
    }

    @PostMapping("/stop")
    @ApiOperation(value = "Stop")
    public void stop(@RequestParam Integer peerId) {
        contextService.stop(peerId);
    }

    @PostMapping("/start")
    @ApiOperation(value = "Start")
    public void start(@RequestParam Integer peerId) {
        contextService.start(peerId);
    }


}
