package com.staj.stock.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.BAD_REQUEST)
public class StockOutOfBoundsException extends RuntimeException {
    public StockOutOfBoundsException(String message) {
        super(message);
    }
}
