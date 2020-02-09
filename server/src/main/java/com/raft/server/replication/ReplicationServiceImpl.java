package com.raft.server.replication;


import com.raft.server.context.Context;
import com.raft.server.context.State;
import com.raft.server.exceptions.NotActiveException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.raft.server.context.State.*;

@Service
@RequiredArgsConstructor
class ReplicationServiceImpl implements ReplicationService {


    private  final Context context;

    @Override
    public AnswerAppendDTO append(RequestAppendDTO requestAppendDTO) {
        if (!context.getActive())
           throw  new NotActiveException();
        context.setState(FOLLOWER);
        return new AnswerAppendDTO(context.getCurrentTerm(),true);
    }


}
