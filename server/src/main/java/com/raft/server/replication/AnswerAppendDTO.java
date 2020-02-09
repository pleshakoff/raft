package com.raft.server.replication;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import javax.validation.constraints.NotNull;

@Getter
public class AnswerAppendDTO {


    private  final Integer id;

    @NotNull
    private  final  Long term;

    @NotNull
    private  final Boolean success;

    @NotNull
    private final HttpStatus statusCode;

    @JsonCreator
    AnswerAppendDTO(
            @JsonProperty("id") Integer id,
            @JsonProperty("term") @NotNull Long term,
            @JsonProperty("success") @NotNull Boolean success) {
        this.id = id;
        this.term = term;
        this.success = success;
        this.statusCode = HttpStatus.OK;
    }

    AnswerAppendDTO(Integer id, @NotNull HttpStatus statusCode) {
        this.id = id;
        this.statusCode = statusCode;
        this.term = null;
        this.success = false;
    }
}
