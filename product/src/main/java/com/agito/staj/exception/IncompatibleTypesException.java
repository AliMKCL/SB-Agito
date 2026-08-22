package com.agito.staj.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class IncompatibleTypesException extends RuntimeException {
    public IncompatibleTypesException(String message) {
        super(message);
    }
}
