package com.raft.server.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.SERVICE_UNAVAILABLE, reason = "Server is not in active mode")
public class NotActiveException extends RuntimeException {
    public NotActiveException() {
        super("Sorry. I am not on duty today :(");
    }
}
