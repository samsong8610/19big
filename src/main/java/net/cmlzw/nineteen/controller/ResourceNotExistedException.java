package net.cmlzw.nineteen.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class ResourceNotExistedException extends RuntimeException{
    public ResourceNotExistedException(String resource) {
        super(String.format("%s not found", resource));
    }
}
