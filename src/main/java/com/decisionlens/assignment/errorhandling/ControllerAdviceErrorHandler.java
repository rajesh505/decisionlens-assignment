package com.decisionlens.assignment.errorhandling;

import com.decisionlens.assignment.exception.BookAlreadyExistsException;
import com.decisionlens.assignment.exception.BookNotFoundException;
import com.decisionlens.assignment.exception.InvalidRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ControllerAdviceErrorHandler {
    private static final String MSG_NOT_FOUND = "Not Found";
    private static final String MSG_INVALID = "Invalid Request";

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity getResponseForInvalidRequest(InvalidRequestException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ErrorResponse.builder()
                        .message(e.getMessage())
                        .build()
        );
    }

    @ExceptionHandler(BookAlreadyExistsException.class)
    public ResponseEntity getResponseForNonUniqueName(BookAlreadyExistsException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                ErrorResponse.builder()
                        .message(e.getMessage())
                        .build()
        );
    }

    @ExceptionHandler(BookNotFoundException.class)
    public ResponseEntity getResponseForResourceNotFound(BookNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(

                ErrorResponse.builder()
                        .message(MSG_NOT_FOUND)
                        .detail(e.getMessage())
                        .build()
        );
    }
}
