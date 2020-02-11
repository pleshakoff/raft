package com.raft.server.log;

import com.raft.server.storage.Entry;
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
@RequestMapping(value = "/data",produces = {MediaType.APPLICATION_JSON_VALUE})
@Api(tags="Data")
@RequiredArgsConstructor
class LogController {

    private final LogService logService;


    @GetMapping
    @ApiOperation(value = "Get all log")
    public List<Operation> all(){
        return logService.all();
    }

    @PostMapping
    @ApiOperation(value = "Insert")
    public String insert(@Valid @RequestBody Entry entry,
                                         BindingResult bindingResult) throws BindException {
        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        }
        logService.insert(entry);
        return "DONE";
    }

    @PostMapping
    @ApiOperation(value = "Sneaky insert")
    public String sneakyInsert(@Valid @RequestBody Entry entry,
                         BindingResult bindingResult) throws BindException {
        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        }
        logService.sneakyInsert(entry);
        return "DONE";
    }


    @PutMapping("/{key}")
    @ApiOperation(value = "Update")
    public String insert(@PathVariable Long key,@RequestParam String val)  {
        logService.update(key,val);
        return "DONE";
    }

    @DeleteMapping("/{key}")
    @ApiOperation(value = "Delete")
    public String delete(@PathVariable Long key)  {
        logService.delete(key);
        return "DONE";
    }


}
