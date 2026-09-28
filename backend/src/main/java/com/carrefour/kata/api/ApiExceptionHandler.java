package com.carrefour.kata.api;

import com.carrefour.kata.application.SubscriptionAlreadyExistsException;
import com.carrefour.kata.application.SubscriptionNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(SubscriptionNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    Map<String, String> handleNotFound(SubscriptionNotFoundException ex) {
        return Map.of("error", ex.getMessage());
    }

    @ExceptionHandler(SubscriptionAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    Map<String, String> handleConflict(SubscriptionAlreadyExistsException ex) {
        return Map.of("error", ex.getMessage());
    }
}
