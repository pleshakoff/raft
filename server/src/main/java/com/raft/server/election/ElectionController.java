package com.raft.server.election;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;


@RestController
@RequestMapping(value = "/election",produces = {MediaType.APPLICATION_JSON_VALUE})
@Api(tags="Election")
@RequiredArgsConstructor
class ElectionController {

    private final ElectionService electionService;

    @PostMapping("/vote")
    @ApiOperation(value = "Request vote")
    public AnswerVoteDTO voteDTO(@Valid @RequestBody RequestVoteDTO requestVoteDTO,
                                               BindingResult bindingResult) throws BindException {
        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        }
        return electionService.vote(requestVoteDTO);
    }

}
