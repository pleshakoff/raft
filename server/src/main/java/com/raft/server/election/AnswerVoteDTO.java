package com.raft.server.election;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
class AnswerVoteDTO {

    private final Integer id;
    private final Long term;
    private  final  boolean voteGranted;
    private final HttpStatus statusCode;

    @JsonCreator
    AnswerVoteDTO(Integer id, Long term, boolean voteGranted) {
        this.id = id;
        this.term = term;
        this.voteGranted = voteGranted;
        this.statusCode = HttpStatus.OK;
    }

    AnswerVoteDTO(Integer id, HttpStatus statusCode) {
        this.id = id;
        this.statusCode = statusCode;
        term = null;
        voteGranted = false;
    }
}
