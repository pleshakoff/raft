package com.raft.server.node;


import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

import static com.raft.server.node.State.FOLLOWER;

@Component
public class NodeStateImpl implements NodeState {

   @Setter
   private State state = FOLLOWER; //TODO make persist
   private AtomicLong currentTerm = new AtomicLong(0L);//TODO make persist

   @Setter
   @Getter
   private Integer votedFor = null;

   private AtomicLong commitIndex =  new AtomicLong(0L);
   private AtomicLong lastApplied  =  new AtomicLong(0L);

   @Value("${raft.vote-time}")
   @Getter
   Integer voteTime;

   @Value("${raft.name}")
   @Getter
   String name;


   @Override
   public State getState() {
      return state;
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
