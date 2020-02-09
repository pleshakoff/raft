package com.raft.server.replication;


import com.network.http.Http;
import com.network.http.HttpException;
import com.raft.server.context.Context;
import com.raft.server.context.Peer;
import com.raft.server.election.ElectionTimer;
import com.raft.server.exceptions.NotActiveException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.raft.server.context.State.*;
import static org.springframework.http.HttpStatus.*;

@Service
@RequiredArgsConstructor
@Slf4j
class ReplicationServiceImpl implements ReplicationService {


    private  final Context context;
    private  final ElectionTimer electionTimer;
    private final Http http;


    private CompletableFuture<AnswerAppendDTO> sendAppendForOnePeer(Integer id) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                log.info("Peer #{} Send append request to {}", context.getId(), id);

                RequestAppendDTO requestAppendDTO = new RequestAppendDTO(context.getCurrentTerm(),
                                                                         context.getId(),
                                                                         0L,0L,
                                                                          context.getCommitIndex()); //TODO Real log indexes

                ResponseEntity<AnswerAppendDTO> response = http.callPost(id.toString(), AnswerAppendDTO.class, requestAppendDTO,"replication", "append");

                return Optional.ofNullable(response.getBody()).
                        orElse(new AnswerAppendDTO(id, NO_CONTENT));
            } catch (HttpException e) {
                log.info("Peer #{} Append request error for {}. Response status code {}", context.getId(), id, e.getStatusCode());
                return new AnswerAppendDTO(id,  e.getStatusCode());
            } catch (Exception e) {
                log.info("Peer #{} Append request error for {}. {} ", context.getId(), id, e.getMessage());
                return new AnswerAppendDTO(id, BAD_REQUEST);
            }

        });
    }


    private List<AnswerAppendDTO> sendAppendToAllPeers(List<Integer> peers) {
        log.info("Peer #{} Spread append request. Peers count: {}", context.getId(),peers.size());
        List<CompletableFuture<AnswerAppendDTO>> answerFutureList =
                peers.stream()
                        .map(this::sendAppendForOnePeer)
                        .collect(Collectors.toList());

            return CompletableFuture.allOf(
                    answerFutureList.toArray(new CompletableFuture[0])
            ).thenApply(v ->
                    answerFutureList.stream().map(CompletableFuture::join).collect(Collectors.toList())
            ).join();
    }


    @Override
    public  void sendAppendEntries(){
        if (!context.getState().equals(LEADER) || !context.getActive()) {
            return;
        }
        log.info("Peer #{} Sending append", context.getId());List<Integer> peersIds = context.getPeers().stream().map(Peer::getId).collect(Collectors.toList());
        List<AnswerAppendDTO> answers = sendAppendToAllPeers(peersIds);
        for (AnswerAppendDTO answer : answers) {
            if (answer.getStatusCode().equals(OK)) {
                if (!context.checkTermGreaterThenCurrent(answer.getTerm())) {
                    return;
                }
            }
        }
    }

    @Override
    public AnswerAppendDTO append(RequestAppendDTO requestAppendDTO) {
        if (!context.getActive())
           throw  new NotActiveException();

        if (requestAppendDTO.getTerm() < context.getCurrentTerm()) {
            return new AnswerAppendDTO(context.getId(), context.getCurrentTerm(),false);
        }
        else if (requestAppendDTO.getTerm() > context.getCurrentTerm()) {
            context.setCurrentTerm(requestAppendDTO.getTerm());
            context.setVotedFor(null);
        }
        electionTimer.reset();
        if (!context.getState().equals(FOLLOWER)) {
            context.setState(FOLLOWER);
        }

        return new AnswerAppendDTO(context.getId(), context.getCurrentTerm(),true);
    }


}
