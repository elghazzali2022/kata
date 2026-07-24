package com.carrefour.kata.infrastructure.web;

import com.carrefour.kata.domain.exception.BusinessRuleException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(BusinessRuleException.class)
    ProblemDetail handleBusinessRule(BusinessRuleException exception) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, exception.getMessage());
        detail.setTitle("Refund request rejected");
        return detail;
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, MaxUploadSizeExceededException.class})
    ProblemDetail handleInvalidRequest(Exception exception) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Invalid request");
        detail.setTitle("Validation error");
        return detail;
    }
}

