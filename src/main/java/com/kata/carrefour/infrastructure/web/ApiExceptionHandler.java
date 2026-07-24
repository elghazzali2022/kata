package com.kata.carrefour.infrastructure.web;

import com.kata.carrefour.domain.exception.BusinessRuleException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(BusinessRuleException.class)
    ProblemDetail handleBusinessRule(
            BusinessRuleException exception
    ) {
        ProblemDetail detail =
                ProblemDetail.forStatusAndDetail(
                        HttpStatus.UNPROCESSABLE_ENTITY,
                        exception.getMessage()
                );

        detail.setTitle("Demande de remboursement rejetée");

        return detail;
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    ProblemDetail handleFileTooLarge(
            MaxUploadSizeExceededException exception
    ) {
        ProblemDetail detail =
                ProblemDetail.forStatusAndDetail(
                        HttpStatus.BAD_REQUEST,
                        "Le fichier dépasse la taille maximale autorisée"
                );

        detail.setTitle("Fichier invalide");

        return detail;
    }
}