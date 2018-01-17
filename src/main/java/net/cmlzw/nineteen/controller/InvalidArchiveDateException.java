package net.cmlzw.nineteen.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidArchiveDateException extends RuntimeException {
    public InvalidArchiveDateException() {
        super("invalid archive date");
    }
}
