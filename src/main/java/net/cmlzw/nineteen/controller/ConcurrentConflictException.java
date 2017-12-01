package net.cmlzw.nineteen.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
public class ConcurrentConflictException extends RuntimeException {
    public ConcurrentConflictException(String msg) {
        super(msg);
    }
}
