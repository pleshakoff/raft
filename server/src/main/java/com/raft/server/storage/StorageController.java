package com.raft.server.storage;

import com.raft.server.operations.Entry;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping(value = "/storage",produces = {MediaType.APPLICATION_JSON_VALUE})
@Api(tags="Storage")
@RequiredArgsConstructor
class StorageController {

    private final StorageService storageService;

    @GetMapping("/{key}")
    @ApiOperation(value = "Get value by key")
    public String get(@PathVariable Long key){
          return storageService.get(key);
    }

    @GetMapping
    @ApiOperation(value = "Get all list")
    public List<Entry> all(){
        return storageService.all();
    }





}
