package net.cmlzw.nineteen.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AlreadySubmittedException extends RuntimeException {
    public AlreadySubmittedException() {
        super("You have submitted quiz result today.");
    }
}
