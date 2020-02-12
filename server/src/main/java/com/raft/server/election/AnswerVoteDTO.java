package com.raft.server.election;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class AnswerVoteDTO {

    @NotNull
    private final Integer id;
    @NotNull
    private final Long term;
    @NotNull
    private  final  boolean voteGranted;
    @NotNull
    private final HttpStatus statusCode;

    @JsonCreator
    AnswerVoteDTO(@JsonProperty("id") Integer id,
                  @JsonProperty("term") Long term,
                  @JsonProperty("voteGranted") boolean voteGranted) {
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
