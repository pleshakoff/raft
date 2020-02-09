package com.raft.server.election;


import com.network.http.Http;
import com.network.http.HttpException;
import com.raft.server.context.Context;
import com.raft.server.context.Peer;
import com.raft.server.exceptions.NotActiveException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.raft.server.context.State.*;
import static org.springframework.http.HttpStatus.*;

@Service
@RequiredArgsConstructor
@Slf4j
 class ElectionServiceImpl implements ElectionService {

    private static final int VOTE_RETRY_DELAY = 2000;
    private final Context context;
    private final Http http;

    private CompletableFuture<AnswerVoteDTO> getVoteFromOnePeer(Integer id,Long term) {
        return CompletableFuture.supplyAsync(() -> {
            if (!checkCurrentElectionStatus(term))
                return new AnswerVoteDTO(id, NO_CONTENT);
            try {
                log.info("Peer #{} Send vote request to {}", context.getId(), id);

                RequestVoteDTO requestVoteDTO = new RequestVoteDTO(term,context.getId(),context.getCommitIndex(),0L); //TODO Right log indexes

                ResponseEntity<AnswerVoteDTO> response = http.callPost(id.toString(), AnswerVoteDTO.class, requestVoteDTO,"election", "vote");

                return Optional.ofNullable(response.getBody()).
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
                        .map(i -> getVoteFromOnePeer(i,term))
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
        if (context.getState().equals(LEADER) || !context.getActive()) {
            return;
        }

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
            for (AnswerVoteDTO answer : answers) {
                if (answer.getStatusCode().equals(OK)) {
                    if (context.checkTermGreaterThenCurrent(answer.getTerm())) {
                        return;
                    }
                    if (answer.isVoteGranted()) {
                        log.info("Peer #{} Vote granted from {}", context.getId(),answer.getId());
                        voteGrantedCount++;
                    } else
                        log.info("Peer #{} Vote reovked from {}", context.getId(),answer.getId());
                        voteRevokedCount++;
                } else {
                    log.info("Peer #{} No vote answer from {}", context.getId(),answer.getId());
                   peersIds.add(answer.getId());
                }
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

    private void winElection(Long term) {
        if (checkCurrentElectionStatus(term))
            log.info("Peer #{} I have WON the election! :)", context.getId());
        context.setState(LEADER);
    }

    private void loseElection(Long term) {
        if (checkCurrentElectionStatus(term))
            log.info("Peer #{} I have LOSE the election! :(", context.getId());
        context.setState(FOLLOWER);
    }


    @Override
    public AnswerVoteDTO vote(RequestVoteDTO requestVoteDTO) {
        log.info("Peer #{} Get vote request from {} with term {}. Current term: {}. Voted for: {}", context.getId(),
                requestVoteDTO.getCandidateId(),
                requestVoteDTO.getTerm(),
                context.getCurrentTerm(),
                context.getVotedFor());

        if (!context.getActive())
            throw  new NotActiveException();

        boolean voteGranted;
        if (requestVoteDTO.getTerm() < context.getCurrentTerm())
            return new AnswerVoteDTO(context.getId(),context.getCurrentTerm(),false);
        else
        if (requestVoteDTO.getTerm().equals(context.getCurrentTerm())) {
            voteGranted = (context.getVotedFor() == null||context.getVotedFor().equals(requestVoteDTO.getCandidateId()));
        }
        else
         voteGranted = context.checkTermGreaterThenCurrent(requestVoteDTO.getTerm());

         //TODO check log
         voteGranted = voteGranted &&
                             (0L<=requestVoteDTO.getLastLogTerm()&&
                              context.getCommitIndex()<=requestVoteDTO.getLastLogIndex());

        if (voteGranted) {
            context.setVotedFor(requestVoteDTO.getCandidateId());
            log.info("Peer #{} Give vote for {}", context.getId(),requestVoteDTO.getCandidateId() );
        }
        else {
            log.info("Peer #{} Reject vote for {}", context.getId(),requestVoteDTO.getCandidateId() );
        }
        return new AnswerVoteDTO(context.getId(),context.getCurrentTerm(),voteGranted);
    }




}
