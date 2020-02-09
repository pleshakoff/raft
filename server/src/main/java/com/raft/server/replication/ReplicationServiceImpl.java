package com.raft.server.replication;


import com.raft.server.context.Context;
import com.raft.server.election.ElectionTimer;
import com.raft.server.exceptions.NotActiveException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
class ReplicationServiceImpl implements ReplicationService {


    private  final Context context;
    private  final ElectionTimer electionTimer;

    @Override
    public AnswerAppendDTO append(RequestAppendDTO requestAppendDTO) {
        if (!context.getActive())
           throw  new NotActiveException();
        context.checkCurrentTerm(requestAppendDTO.getTerm());
        electionTimer.reset();




        return new AnswerAppendDTO(context.getCurrentTerm(),true);
    }


}
