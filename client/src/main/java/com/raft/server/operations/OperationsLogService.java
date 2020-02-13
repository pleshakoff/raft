package com.raft.server.operations;

import java.util.List;

interface OperationsLogService {


    List<Operation> all(Integer peerId);

   }
