package com.raft.server.election;


import com.network.http.Http;
import com.network.http.HttpException;
import com.raft.server.context.Context;
import com.raft.server.context.State;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;

import static com.raft.server.context.State.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ElectionServiceImpl implements ElectionService {

    private final Context context;
    private final Http http;

    CompletableFuture<AnswerVoteDTO> getVoteFromPeer(Integer id) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                ResponseEntity<AnswerVoteDTO> answerVoteDTOResponseEntity = http.callGet(id.toString(), AnswerVoteDTO.class, "election", "vote");
                return Optional.ofNullable(answerVoteDTOResponseEntity.getBody()).
                          orElse(new AnswerVoteDTO(id, HttpStatus.NO_CONTENT));
            } catch (HttpException e) {
                return new AnswerVoteDTO(id, e.getStatusCode());
            }
        });
    }

    @Override
    public void startElection() {
        log.info("Peer #{} START ELECTION", context.getId());
        context.setState(CANDIDATE);
        context.incCurrentTerm();
    }

    @Override
    public void interruptElection() {
        context.setState(FOLLOWER);
    }


    @Override
    public void vote() {

    }


}
