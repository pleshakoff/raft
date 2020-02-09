package com.raft.server.replication;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@RequiredArgsConstructor
class AnswerAppendDTO {

    @NotNull
    private  final  Long term;
    @NotNull
    private  final Boolean success;
}
