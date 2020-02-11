package com.raft.server.replication;


import com.network.http.Http;
import com.network.http.HttpException;
import com.raft.server.context.ContextDecorator;
import com.raft.server.context.Peer;
import com.raft.server.data.Operation;
import com.raft.server.data.OperationsLog;
import com.raft.server.election.timer.ResetElectionTimerEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.raft.server.context.State.FOLLOWER;
import static org.springframework.http.HttpStatus.*;

@Service
@RequiredArgsConstructor
@Slf4j
class ReplicationServiceImpl implements ReplicationService {


    private final ContextDecorator context;
    private ApplicationEventPublisher applicationEventPublisher;
    private final OperationsLog operationsLog;
    private final Http http;


    private CompletableFuture<AnswerAppendDTO> sendAppendForOnePeer(Integer id) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                log.info("Peer #{} Send append request to {}", context.getId(), id);

                Peer peer = context.getPeer(id);


                //• If last log index ≥ nextIndex for a follower: send
                //AppendEntries RPC with log entries starting at nextIndex
                Operation operation = (peer.getNextIndex() <= operationsLog.getLastIndex()) ? operationsLog.get(peer.getNextIndex()) : null;

                RequestAppendDTO requestAppendDTO = new RequestAppendDTO(
                        context.getCurrentTerm(),
                        context.getId(),
                        peer.getNextIndex() - 1,
                        operationsLog.getTerm(peer.getNextIndex() - 1),
                        context.getCommitIndex(),
                        operation
                );

                ResponseEntity<AnswerAppendDTO> response = http.callPost(id.toString(), AnswerAppendDTO.class, requestAppendDTO, "replication", "append");

                return Optional.ofNullable(response.getBody()).
                        orElse(new AnswerAppendDTO(id, NO_CONTENT));
            } catch (HttpException e) {
                log.info("Peer #{} Append request error for {}. Response status code {}", context.getId(), id, e.getStatusCode());
                return new AnswerAppendDTO(id, e.getStatusCode());
            } catch (Exception e) {
                log.info("Peer #{} Append request error for {}. {} ", context.getId(), id, e.getMessage());
                return new AnswerAppendDTO(id, BAD_REQUEST);
            }

        });
    }


    private List<AnswerAppendDTO> sendAppendToAllPeers(List<Integer> peers) {
        log.debug("Peer #{} Forward append request to peers. Peers count: {}", context.getId(), peers.size());
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
    public void appendRequest() {
        log.debug("Peer #{} Sending append request", context.getId());
        List<Integer> peersIds = context.getPeers().stream().map(Peer::getId).collect(Collectors.toList());
        List<AnswerAppendDTO> answers = sendAppendToAllPeers(peersIds);
        for (AnswerAppendDTO answer : answers) {
            if (answer.getStatusCode().equals(OK)) {
                if (answer.getTerm() > context.getCurrentTerm()) {
                    //• If RPC request or response contains term T > currentTerm: set currentTerm = T, convert to follower (§5.1)
                    context.setTermGreaterThenCurrent(answer.getTerm());
                    return;
                }
                Peer peer = context.getPeer(answer.getId());
                if (answer.getSuccess()) {
                    //If successful: update nextIndex and matchIndex for follower
                    log.info("Peer #{} Get append success  from {}", context.getId(),answer.getId());
                    peer.setNextIndex(answer.getMatchIndex()+1);
                    peer.setMatchIndex(answer.getMatchIndex());
                }
                else
                {
                    //If AppendEntries fails because of log inconsistency:decrement nextIndex and retry
                    log.info("Peer #{} Get append fault from {}", context.getId(),answer.getId());
                    peer.decNextIndex();
                }
            }
        }
    }

    @Override
    public AnswerAppendDTO append(RequestAppendDTO dto) {
        // Invoked by leader to replicate log entries (§5.3); also used as heartbeat (§5.2).

        context.cancelIfNotActive();

        // Reply false if term < currentTerm (§5.1)
        if (dto.getTerm() < context.getCurrentTerm()) {
            log.debug("Peer #{} Rejected append from {}. Term too small", context.getId(), dto.getLeaderId());
            return new AnswerAppendDTO(context.getId(), context.getCurrentTerm(), false, null);
        } else if (dto.getTerm() > context.getCurrentTerm()) {
            //If RPC request or response contains term T > currentTerm: set currentTerm = T,
            context.setCurrentTerm(dto.getTerm());
            context.setVotedFor(null);
        }
        applicationEventPublisher.publishEvent(new ResetElectionTimerEvent(this));
        // convert to follower. Just one Leader RULE
        if (!context.getState().equals(FOLLOWER)) {
            context.setState(FOLLOWER);
        }

//        2. Reply false if log doesn’t contain an entry at prevLogIndex
//        whose term matches prevLogTerm (§5.3)
        if (!dto.getPrevLogTerm().equals(operationsLog.getTerm(dto.getPrevLogIndex()))) {
            log.info("Peer #{} Rejected append from {}. Log doesn't contain prev term. Current term {}, Candidate term {} ",
                    context.getId(),dto.getLeaderId(),context.getCurrentTerm(),dto.getTerm());
            return new AnswerAppendDTO(context.getId(), context.getCurrentTerm(), false, null);
        }


//        3. If an existing entry conflicts with a new one (same index
//                but different terms), delete the existing entry and all that
//        follow it (§5.3)
        int newOperationIndex = dto.getPrevLogIndex() + 1;
        if ((operationsLog.getLastIndex() <= newOperationIndex) &&
                (!dto.getOperation().getTerm().equals(operationsLog.getTerm(newOperationIndex)))) {
            operationsLog.removeAllFromIndex(newOperationIndex);
        }


//        4. Append any new entries not already in the log
        operationsLog.append(dto.getOperation());

//        5. If leaderCommit > commitIndex, set commitIndex =
//                min(leaderCommit, index of last new entry)
        if (dto.getLeaderCommit()>context.getCommitIndex())
            context.setCommitIndex(Math.min(dto.getLeaderCommit(),operationsLog.getLastIndex()));
        return new AnswerAppendDTO(context.getId(), context.getCurrentTerm(), true, operationsLog.getLastIndex());
    }


}
