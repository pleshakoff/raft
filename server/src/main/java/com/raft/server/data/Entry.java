package com.raft.server.data;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotNull;

@RequiredArgsConstructor
@Getter
class Entry {

    @NotNull
    private final Long key;
    private final String val;

}
