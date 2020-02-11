package com.raft.server.data;

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
class DataController {

    private final DataService dataService;

    @GetMapping("/{key}")
    @ApiOperation(value = "Get value by key")
    public String get(@PathVariable Long key){
          return dataService.get(key);
    }

    @GetMapping
    @ApiOperation(value = "Get all list")
    public List<Entry> all(){
        return dataService.all();
    }


    @PostMapping
    @ApiOperation(value = "Insert")
    public String insert(@Valid @RequestBody Entry entry,
                                         BindingResult bindingResult) throws BindException {
        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        }
        dataService.insert(entry);
        return "DONE";
    }

    @PutMapping("/{key}")
    @ApiOperation(value = "Update")
    public String insert(@PathVariable Long key,@RequestParam String val)  {
        dataService.update(key,val);
        return "DONE";
    }

    @DeleteMapping("/{key}")
    @ApiOperation(value = "Update")
    public String delete(@PathVariable Long key)  {
        dataService.delete(key);
        return "DONE";
    }


}
