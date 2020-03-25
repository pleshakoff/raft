package com.raft.server.election;


import com.network.http.Http;
import com.network.http.HttpException;
import com.raft.server.context.Context;
import com.raft.server.node.peers.Peer;
import com.raft.server.operations.OperationsLog;
import com.raft.server.replication.AnswerAppendDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.raft.server.node.State.*;
import static org.springframework.http.HttpStatus.*;

@Service
@RequiredArgsConstructor
@Slf4j
class ElectionServiceImpl implements ElectionService {

    private static final int VOTE_RETRY_DELAY = 2000;
    private final Context context;
    private final Http http;
    private final OperationsLog operationsLog;


    private CompletableFuture<AnswerVoteDTO> getVoteFromOnePeer(Integer id,
                                                                Long term) {
        return CompletableFuture.supplyAsync(() -> {
            if (!checkCurrentElectionStatus(term))
                return new AnswerVoteDTO(id, NO_CONTENT);
            try {
                log.info("Peer #{} Send vote request to {}", context.getId(), id);

                RequestVoteDTO requestVoteDTO = new RequestVoteDTO(term, context.getId(),
                                                                   operationsLog.getLastIndex(),
                                                                   operationsLog.getLastTerm());
                ResponseEntity<AnswerVoteDTO> response = http.callPost(id.toString(), AnswerVoteDTO.class,
                                                                       requestVoteDTO, "election", "vote");

                return Optional.ofNullable(response.getBody()).
                        orElse(new AnswerVoteDTO(id, NO_CONTENT));
            } catch (HttpException e) {
                log.error("Peer #{} Vote request error for {}. Response status code {}", context.getId(), id,
                          e.getStatusCode());
                return new AnswerVoteDTO(id, e.getStatusCode());
            } catch (ResourceAccessException e) {
                log.error("Peer #{} Vote request error for {}. {} {} ", context.getId(), id, e.getClass(),
                          e.getMessage());
                return new AnswerVoteDTO(id, SERVICE_UNAVAILABLE);
            } catch (Exception e) {
                log.error("Peer #{} Vote request error for {}. {} {} ", context.getId(), id, e.getClass(),
                          e.getMessage());
                return new AnswerVoteDTO(id, INTERNAL_SERVER_ERROR);
            }

        });
    }

    private List<AnswerVoteDTO> getVoteFromAllPeers(Long term,
                                                    List<Integer> peers) {
        log.debug("Peer #{} Forward vote request to peers. Term {}. Peers count: {}", context.getId(), term,
                  peers.size());
        List<CompletableFuture<AnswerVoteDTO>> answerFutureList =
                peers.stream()
                        .map(i -> getVoteFromOnePeer(i, term))
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
        //Invoked by candidates to gather votes
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
                    if (answer.getTerm() > context.getCurrentTerm()) {
                        //• If RPC request or response contains term T > currentTerm: set currentTerm = T, convert to follower (§5.1)
                        context.setTermGreaterThenCurrent(answer.getTerm());
                        return;
                    }
                    if (answer.isVoteGranted()) {
                        log.info("Peer #{} Vote granted from {}", context.getId(), answer.getId());
                        context.getPeer(answer.getId()).setVoteGranted(true);
                        voteGrantedCount++;
                    } else
                        log.info("Peer #{} Vote revoked from {}", context.getId(), answer.getId());
                    voteRevokedCount++;
                } else {
                    log.info("Peer #{} No vote answer from {}", context.getId(), answer.getId());
                    peersIds.add(answer.getId());
                }
            }
            if (voteGrantedCount >= context.getQuorum()) {
                winElection(term);
                return;
            } else if (voteRevokedCount >= context.getQuorum()) {
                loseElection(term);
                return;
            } //else retry
            delay();
        }
    }

    private void delay() {
        try {
            log.info("Peer #{} Preparing to retry vote request", context.getId());
            Thread.sleep(VOTE_RETRY_DELAY);
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
    }

    private boolean checkCurrentElectionStatus(Long term) {
        return term.equals(context.getCurrentTerm()) && context.getState().equals(CANDIDATE);
    }

    private void winElection(Long term) {
        if (checkCurrentElectionStatus(term)) {
            log.info("Peer #{} I have WON the election! :)", context.getId());
            context.setState(LEADER);

//        for each server, index of the next operations entry  to send to that server
//        (initialized to leader last operations index + 1)
            context.getPeers().forEach(peer ->
                                               peer.setNextIndex(operationsLog.getLastIndex() + 1)

            );
        }

    }

    private void loseElection(Long term) {
        if (checkCurrentElectionStatus(term)) {
            log.info("Peer #{} I have LOSE the election! :(", context.getId());
            context.setState(FOLLOWER);
        }

    }


    @Override
    public AnswerVoteDTO vote(RequestVoteDTO dto) {
        context.cancelIfNotActive();
        log.info("Peer #{} Get vote request from {} with term {}. Current term: {}. Voted for: {}", context.getId(),
                 dto.getCandidateId(),
                 dto.getTerm(),
                 context.getCurrentTerm(),
                 context.getVotedFor());


//        1. Reply false if term < currentTerm (§5.1)
//        2. If votedFor is null or candidateId, and candidate’s operations is at
//        least as up-to-date as receiver’s operations, grant vote (§5.2, §5.4)

        boolean termCheck;
        if (dto.getTerm() < context.getCurrentTerm())
            return new AnswerVoteDTO(context.getId(), context.getCurrentTerm(), false);
        else if (dto.getTerm().equals(context.getCurrentTerm())) {
            termCheck = (context.getVotedFor() == null || context.getVotedFor().equals(dto.getCandidateId()));
        } else {
            //• If RPC request or response contains term T > currentTerm: set currentTerm = T, convert to follower (§5.1)
            termCheck = true;
            context.setTermGreaterThenCurrent(dto.getTerm());
        }

        boolean logCheck = !((operationsLog.getLastTerm() > dto.getLastLogTerm()) ||
                ((operationsLog.getLastTerm().equals(dto.getLastLogTerm())) &&
                        (operationsLog.getLastIndex() > dto.getLastLogIndex())));


        boolean voteGranted = termCheck && logCheck;

        if (voteGranted) {
            context.setVotedFor(dto.getCandidateId());
            log.info("Peer #{} Give vote for {}", context.getId(), dto.getCandidateId());
        } else {
            log.info("Peer #{} Reject vote for {} Current term {}, Candidate term {} ", context.getId(),
                     dto.getCandidateId(),
                     context.getCurrentTerm(), dto.getTerm());
        }
        return new AnswerVoteDTO(context.getId(), context.getCurrentTerm(), voteGranted);
    }


}
