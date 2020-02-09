package com.raft.server.storage;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class Operation {

    private final Long Id;
    private  final  Long term;
    private  final  OperationType type;
    private  final  Entry entry;
}
