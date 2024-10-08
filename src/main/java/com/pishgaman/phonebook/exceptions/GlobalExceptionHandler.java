package com.pishgaman.phonebook.exceptions;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DatabaseIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    RestErrorResponse handleDatabaseIntegrityViolationException(
            DatabaseIntegrityViolationException ex) {
        return new RestErrorResponse(
                HttpStatus.CONFLICT.value(),
                ex.getMessage(),
                LocalDateTime.now());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    RestErrorResponse handleEntityNotFoundException(
            EntityNotFoundException ex) {
        return new RestErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                LocalDateTime.now());
    }
    //handle IllegalArgumentException
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    RestErrorResponse handleIllegalArgumentException(
            IllegalArgumentException ex) {
        return new RestErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                LocalDateTime.now());
    }




    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    RestErrorResponse handleException(Exception ex) {
        return new RestErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                LocalDateTime.now());
    }
}
