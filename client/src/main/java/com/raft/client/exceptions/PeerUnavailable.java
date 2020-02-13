package com.raft.client.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.SERVICE_UNAVAILABLE, reason = "Peer unavailable")
public class PeerUnavailable extends RuntimeException {

    public PeerUnavailable() {
        super("Sorry. I am not on duty today :(");
    }
}
