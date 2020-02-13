package com.raft.server.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Leader NOT found")
public class LeaderNotFoundException extends RuntimeException {

    public LeaderNotFoundException() {
        super("Sorry. I am not on duty today :(");
    }
}
