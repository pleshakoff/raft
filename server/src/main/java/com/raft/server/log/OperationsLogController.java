package com.raft.server.log;

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
@RequestMapping(value = "/log",produces = {MediaType.APPLICATION_JSON_VALUE})
@Api(tags="Log")
@RequiredArgsConstructor
class OperationsLogController {

    private static final String DONE = "{\"result\": \"DONE\"}";
    private final OperationsLogService operationsLogService;


    @GetMapping
    @ApiOperation(value = "Get all log")
    public List<Operation> all(){
        return operationsLogService.all();
    }



    @PostMapping
    @ApiOperation(value = "Insert")
    public String insert(@Valid @RequestBody Entry entry,
                                         BindingResult bindingResult) throws BindException {
        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        }
        operationsLogService.insert(entry);
        return DONE;
    }

    @PostMapping("/sneaky")
    @ApiOperation(value = "Sneaky insert")
    public String sneakyInsert(@Valid @RequestBody Entry entry,
                         BindingResult bindingResult) throws BindException {
        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        }
        operationsLogService.sneakyInsert(entry);
        return DONE;
    }


    @PutMapping("/{key}")
    @ApiOperation(value = "Update")
    public String insert(@PathVariable Long key,@RequestParam String val)  {
        operationsLogService.update(key, val);
        return  DONE;
    }

    @DeleteMapping("/{key}")
    @ApiOperation(value = "Delete")
    public String delete(@PathVariable Long key)  {
        operationsLogService.delete(key);
        return  DONE;
    }


}
