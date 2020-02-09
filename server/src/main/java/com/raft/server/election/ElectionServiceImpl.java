package com.raft.server.election;


import com.network.http.Http;
import com.network.http.HttpException;
import com.raft.server.context.Context;
import com.raft.server.context.Peer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.raft.server.context.State.*;
import static org.springframework.http.HttpStatus.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ElectionServiceImpl implements ElectionService {

    private static final int VOTE_RETRY_DELAY = 2000;
    private final Context context;
    private final Http http;

    private CompletableFuture<AnswerVoteDTO> getVoteFromOnePeer(Integer id) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                log.info("Peer #{} Send vote request to {}", context.getId(), id);
                ResponseEntity<AnswerVoteDTO> answerVoteDTOResponseEntity = http.callGet(id.toString(), AnswerVoteDTO.class, "election", "vote");
                return Optional.ofNullable(answerVoteDTOResponseEntity.getBody()).
                        orElse(new AnswerVoteDTO(id, NO_CONTENT));
            } catch (HttpException e) {
                log.info("Peer #{} Vote request error for {}. Response status code {}", context.getId(), id, e.getStatusCode());
                return new AnswerVoteDTO(id, e.getStatusCode());
            } catch (Exception e) {
                log.info("Peer #{} Vote request error for {}. {} ", context.getId(), id, e.getMessage());
                return new AnswerVoteDTO(id, BAD_REQUEST);
            }

        });
    }

    private List<AnswerVoteDTO> getVoteFromAllPeers(Long term, List<Integer> peers) {
        log.info("Peer #{} Spread vote request. Term {}. Peers count: {}", context.getId(), term, peers.size());
        List<CompletableFuture<AnswerVoteDTO>> answerFutureList =
                peers.stream()
                        .map(this::getVoteFromOnePeer)
                        .collect(Collectors.toList());

        if (checkCurrentElectionStatus(term)) {
            return CompletableFuture.allOf(
                    answerFutureList.toArray(new CompletableFuture[0])
            ).thenApply(v ->
                    answerFutureList.stream().map(CompletableFuture::join).collect(Collectors.toList())
            ).join();
        } else
            return new ArrayList<>();
    }


    @Override
    public void processElection() {
        log.info("Peer #{} Start election", context.getId());

        context.setState(CANDIDATE);
        Long term = context.incCurrentTerm();
        context.setVotedFor(context.getId());

        List<Integer> peersIds = context.getPeers().stream().map(Peer::getId).collect(Collectors.toList());
        long voteGrantedCount = 1L;
        long voteRevokedCount = 0L;

        //while didn't get heartbeat from leader or new election started
        while (checkCurrentElectionStatus(term)) {
            List<AnswerVoteDTO> answers = getVoteFromAllPeers(term, peersIds);
            peersIds = new ArrayList<>();
            for (AnswerVoteDTO voteDTO : answers) {
                if (voteDTO.getStatusCode().equals(OK)) {
                    if (voteDTO.isVoteGranted()) {
                        voteGrantedCount++;
                    } else
                        voteRevokedCount++;
                } else
                    peersIds.add(voteDTO.getId());
            }
            if (voteGrantedCount >= context.getQuorum()) {
                winElection(term);
                return;
            } else if (voteRevokedCount >= context.getQuorum()) {
                loseElection(term);
                return;
            }
            delay();
        }
    }

    private void delay() {
        try {
            log.info("Peer #{} Preparing to retry vote request", context.getId());
            Thread.sleep(VOTE_RETRY_DELAY);
        } catch (InterruptedException e) {
            log.error(e.getMessage(),e);
        }
    }

    private boolean checkCurrentElectionStatus(Long term) {
        return term.equals(context.getCurrentTerm()) && context.getState().equals(CANDIDATE);
    }

    @Override
    public void winElection(Long term) {
        if (checkCurrentElectionStatus(term))
            log.info("Peer #{} I have WON the election!", context.getId());
        context.setState(LEADER);
    }

    @Override
    public void loseElection(Long term) {
        if (checkCurrentElectionStatus(term))
            log.info("Peer #{} I have LOSE the election!", context.getId());
        context.setState(FOLLOWER);
    }


    @Override
    public void interruptElection() {
        context.setState(FOLLOWER);
    }


    @Override
    public void vote() {

    }


}
