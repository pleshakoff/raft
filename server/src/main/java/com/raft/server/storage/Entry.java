package com.raft.server.storage;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class Entry {

    private final Long key;
    private final String val;

}
