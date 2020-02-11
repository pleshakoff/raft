package com.raft.server.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Server not in LEADER mode")
public class NotLeaderException extends RuntimeException {

    public NotLeaderException() {
        super("Sorry. I am not on duty today :(");
    }
}
