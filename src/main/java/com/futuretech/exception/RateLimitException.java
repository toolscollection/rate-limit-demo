package com.futuretech.exception;

import org.springframework.http.HttpStatus;

public class RateLimitException extends RuntimeException {
    public RateLimitException(String message) {
        super(message);
    }
}

