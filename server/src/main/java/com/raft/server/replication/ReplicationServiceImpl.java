package com.raft.server.replication;


import com.network.http.Http;
import com.network.http.HttpException;
import com.raft.server.context.Context;
import com.raft.server.node.peers.Peer;
import com.raft.server.operations.Operation;
import com.raft.server.operations.OperationsLog;
import com.raft.server.election.timer.ResetElectionTimerEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.raft.server.node.State.FOLLOWER;
import static org.springframework.http.HttpStatus.*;

@Service
@RequiredArgsConstructor
@Slf4j
class ReplicationServiceImpl implements ReplicationService {


    private final Http http;
    private final Context context;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final OperationsLog operationsLog;


    private List<AnswerAppendDTO> sendAppendToAllPeers(List<Integer> peers) {
        //operations.debug("Peer #{} Forward request to peers. Peers count: {}", attributes.getId(), peers.size());
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

    private CompletableFuture<AnswerAppendDTO> sendAppendForOnePeer(Integer id) {
        return CompletableFuture.supplyAsync(() -> {
            String opNameForLog = "Heartbeat";
            try {

                Peer peer = context.getPeer(id);

                //• If last operations index ≥ nextIndex for a follower: send
                //AppendEntries RPC with operations entries starting at nextIndex
                Operation operation;
                if (peer.getNextIndex() <= operationsLog.getLastIndex()) {
                    opNameForLog = "Append";
                    operation = operationsLog.get(peer.getNextIndex());
                    log.info("Peer #{} {} request to {}. Peer next index: {}. Log last index:{} ",
                             context.getId(),opNameForLog,id,peer.getNextIndex(),operationsLog.getLastIndex());
                }
                else {
                    operation = null;
                    log.info("Peer #{} {} request  to {}", context.getId(),opNameForLog,id);
                }


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
                log.info("Peer #{} {} request error for {}. Response status code {}", context.getId(),opNameForLog, id, e.getStatusCode());
                return new AnswerAppendDTO(id, e.getStatusCode());
            } catch (Exception e) {
                log.info("Peer #{} {} request error for {}. {} {} ", context.getId(),opNameForLog,id,e.getClass() ,e.getMessage());
                return new AnswerAppendDTO(id, BAD_REQUEST);
            }

        });
    }


    @Override
    public void appendRequest() {
        log.debug("Peer #{} Sending request to peers", context.getId());
        List<Integer> peersIds = context.getPeers().stream().map(Peer::getId).collect(Collectors.toList());

        while (peersIds.size()>0) {//retry until get success from all peers
            List<AnswerAppendDTO> answers = sendAppendToAllPeers(peersIds);
            peersIds = new ArrayList<>();
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
                        log.debug("Peer #{} Get \"request success\"  from {}", context.getId(), answer.getId());
                                log.debug("Peer #{} Set next index for {} Next: {} Match: {}. Old match:{}", context.getId(), answer.getId(),answer.getMatchIndex() + 1,answer.getMatchIndex(),peer.getMatchIndex());
                                peer.setNextIndex(answer.getMatchIndex() + 1);
                                peer.setMatchIndex(answer.getMatchIndex());

                    } else {
                        //If AppendEntries fails because of operations inconsistency:decrement nextIndex and retry
                        log.debug("Peer #{} Get request fault from {} and decrement current next index {} ", context.getId(), answer.getId(),peer.getNextIndex());
                        peer.decNextIndex();
                        peersIds.add(answer.getId());
                    }
                }
            }
            tryToCommit();

        }
    }
    
    private void  tryToCommit() {
//        If there exists an N such that N > commitIndex, a majority
//        of matchIndex[i] ≥ N, and operations[N].term == currentTerm:
//        set commitIndex = N (§5.3, §5.4).

        log.debug("Peer #{} trying to commit operations. Current commit index {}", context.getId(),context.getCommitIndex());
        while (true) {
            int N =  context.getCommitIndex()+1;
            long count = context.getPeers().stream().map(Peer::getMatchIndex).
                    filter(matchIndex -> matchIndex >=N).count()+1;//followers plus leader
            if (count>=context.getQuorum() &&
                operationsLog.getLastIndex()>=N &&
                operationsLog.getTerm(N).equals(context.getCurrentTerm()))
            {
                context.setCommitIndex(N);
            }
            else
              return;
        }


    }
    

    @Override
    public AnswerAppendDTO append(RequestAppendDTO dto) {
        // Invoked by leader to replicate operations entries (§5.3); also used as heartbeat (§5.2).

        context.cancelIfNotActive();

        // Reply false if term < currentTerm (§5.1)
        if (dto.getTerm() < context.getCurrentTerm()) {
            log.debug("Peer #{} Rejected opName from {}. Term too small", context.getId(), dto.getLeaderId());
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

//        2. Reply false if operations does not contain an entry at prevLogIndex
//        whose term matches prevLogTerm (§5.3)
        if ((dto.getPrevLogIndex()>operationsLog.getLastIndex())|| !dto.getPrevLogTerm().equals(operationsLog.getTerm(dto.getPrevLogIndex()))) {
            log.debug("Peer #{} Rejected opName from {}. Log doesn't contain prev term. Current term {}, Candidate term {} ",
                    context.getId(), dto.getLeaderId(), context.getCurrentTerm(), dto.getTerm());
            return new AnswerAppendDTO(context.getId(), context.getCurrentTerm(), false, null);
        }


//        3. If an existing entry conflicts with a new one (same index
//                but different terms), delete the existing entry and all that
//        follow it (§5.3)
        String opName = "heartbeat";
        Operation newOperation = dto.getOperation();
        if (newOperation !=null) {
            int newOperationIndex = dto.getPrevLogIndex() + 1;
            log.info("Peer #{} checking new operation. New index {}. Operation term: {}. Last index: {} ",
                     context.getId(), newOperationIndex,newOperation.getTerm(), operationsLog.getLastIndex());
            if ((newOperationIndex <= operationsLog.getLastIndex()) &&
                 (!newOperation.getTerm().equals(operationsLog.getTerm(newOperationIndex)))) {
                operationsLog.removeAllFromIndex(newOperationIndex);
            }
//        4. Append any new entries not already in the operations
            operationsLog.append(newOperation);
            opName = "opName";
        }
//        5. If leaderCommit > commitIndex, set commitIndex =
//                min(leaderCommit, index of last new entry)
        if (dto.getLeaderCommit() > context.getCommitIndex()) {
            context.setCommitIndex(Math.min(dto.getLeaderCommit(), operationsLog.getLastIndex()));
        }

        log.debug("Peer #{}. Success answer to {} request. Term: {}. Mach index {}", context.getId(), opName, context.getCurrentTerm(), operationsLog.getLastIndex());
        return new AnswerAppendDTO(context.getId(), context.getCurrentTerm(), true, operationsLog.getLastIndex());
    }


}
