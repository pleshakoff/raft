package com.raft.client.storage;

import com.raft.client.operations.Entry;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping(value = "/storage", produces = {MediaType.APPLICATION_JSON_VALUE})
@Api(tags = "Storage")
@RequiredArgsConstructor
class StorageController {

    private final StorageService storageService;

    @GetMapping("/{key}")
    @ApiOperation(value = "Get value by key")
    public String get(@PathVariable Long key,
                      @RequestParam Integer peerId) {
        return storageService.get(key, peerId);
    }


    @GetMapping
    @ApiOperation(value = "Get all list")
    public List<Entry> all(@RequestParam Integer peerId) {
        return storageService.all(peerId);
    }


    @PostMapping
    @ApiOperation(value = "Insert")
    public String insert(@Valid @RequestBody Entry entry,
                         BindingResult bindingResult) throws BindException {
        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        }
        return storageService.insert(entry);
    }

    @PutMapping("/{key}")
    @ApiOperation(value = "Update")
    public String insert(@PathVariable Long key,
                         @RequestBody String val) {
        return storageService.update(key, val);
    }

    @DeleteMapping("/{key}")
    @ApiOperation(value = "Delete")
    public String delete(@PathVariable Long key) {
        return storageService.delete(key);
    }


}
