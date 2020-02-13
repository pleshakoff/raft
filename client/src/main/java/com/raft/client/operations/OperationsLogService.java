package com.raft.client.operations;

import java.util.List;

interface OperationsLogService {


    List<Operation> all(Integer peerId);

   }
