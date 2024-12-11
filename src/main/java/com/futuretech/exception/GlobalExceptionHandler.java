package com.futuretech.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RateLimitException.class)
    public Map<String, Object> handleRateLimitException(RateLimitException e) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", HttpStatus.TOO_MANY_REQUESTS.value());
        response.put("message", e.getMessage());
        return response;
    }
}
