package com.raft.server.exchange;

import com.network.ServicesProps;
import com.network.http.Http;
import com.network.http.HttpException;
import com.raft.server.context.Context;
import com.raft.server.exceptions.LeaderNotFoundException;
import com.raft.server.exceptions.PeerUnavailable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExchangeServiceImpl implements ExchangeService {

    private final ServicesProps servicesProps;
    private final Http http;


    private CompletableFuture<Context> requestContextFromOnePeer(String id) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return http.callGet(id, Context.class, "context").getBody();

            } catch (HttpException e) {
                log.error("Server request error for peer #{}. Response status code {}", id, e.getStatusCode());
                return null;
            } catch (Exception e) {
                log.error("Server request error for peer #{}. Response status code {} {}", id, e.getClass(), e.getMessage());
                return null;
            }
        });
    }


    @Override
    public List<Context> requestContextFromAllPeers() {
        log.info("Get context data from all peers");
        List<CompletableFuture<Context>> answerFutureList =
                servicesProps.getServices().stream()
                        .map(com.network.Service::getName)
                        .map(this::requestContextFromOnePeer)
                        .collect(Collectors.toList());

            return CompletableFuture.allOf(
                    answerFutureList.toArray(new CompletableFuture[0])
            ).thenApply(v ->answerFutureList.stream().map(CompletableFuture::join).
                    filter(Objects::nonNull).
                    collect(Collectors.toList())
            ).join();
    }

    @Override
    public Integer getLeaderId() {
        return requestContextFromAllPeers().stream().
                filter(context -> context.isLeader() && context.getActive()).
                filter(Context::getActive).
                map(Context::getId).findFirst().orElseThrow(LeaderNotFoundException::new);
    }

    @Override
    public void checkAvailable(Integer peerId) {
         if (requestContextFromAllPeers().stream().noneMatch(context -> context.getId().equals(peerId) && context.getActive()))
         {
             throw new  PeerUnavailable();
         }
    }


}
