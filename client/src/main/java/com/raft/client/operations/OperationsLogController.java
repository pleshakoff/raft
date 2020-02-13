package com.raft.client.operations;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping(value = "/log",produces = {MediaType.APPLICATION_JSON_VALUE})
@Api(tags="Log")
@RequiredArgsConstructor
class OperationsLogController {

    private final OperationsLogService operationsLogService;


    @GetMapping
    @ApiOperation(value = "Get all operations")
    public List<Operation> all(@RequestParam Integer peerId){
        return operationsLogService.all(peerId);
    }







}
