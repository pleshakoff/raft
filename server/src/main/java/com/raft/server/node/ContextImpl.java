package com.raft.server.node;


import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

import static com.raft.server.node.State.FOLLOWER;

@Component
@Slf4j
public class ContextImpl implements Context {


   @Value("${raft.id}")
   @Getter
   Integer id;

   @Setter
   @Getter
   Boolean active=true;

   private State state = FOLLOWER; //TODO make persist
   private AtomicLong currentTerm = new AtomicLong(0L);//TODO make persist

   @Setter
   @Getter
   private Integer votedFor = null;

   private AtomicLong commitIndex =  new AtomicLong(0L);
   private AtomicLong lastApplied  =  new AtomicLong(0L);

   @Value("${raft.election-timeout}")
   @Getter
   Integer electionTimeout;

   @Override
   public State getState() {
      return state;
   }

   @Override
   public void setState(State state) {
      log.info("Peer #{} Get new state: {}",getId(),state);
      this.state = state;
   }

   @Override
   public Long getCurrentTerm() {
      return currentTerm.get();
   }

   @Override
   public void incCurrentTerm(){
      currentTerm.incrementAndGet();
   };

   @Override
   public Long getCommitIndex() {
      return commitIndex.get();
   }

   @Override
   public void incCommitIndex(){
      commitIndex.incrementAndGet();
   };

   @Override
   public Long getLastApplied() {
      return lastApplied.get();
   }

   @Override
   public void incLastApplied(){
      lastApplied.incrementAndGet();
   };

}
