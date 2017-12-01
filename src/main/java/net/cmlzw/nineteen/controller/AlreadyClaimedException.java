package net.cmlzw.nineteen.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AlreadyClaimedException extends RuntimeException {
    public AlreadyClaimedException() {
        super("The award has already bean claimed.");
    }
}
