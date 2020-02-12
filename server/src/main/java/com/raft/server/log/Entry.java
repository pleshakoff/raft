package com.raft.server.log;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotNull;

@RequiredArgsConstructor
@Getter
public class Entry {

    @NotNull
    private final Long key;
    private final String val;

}
