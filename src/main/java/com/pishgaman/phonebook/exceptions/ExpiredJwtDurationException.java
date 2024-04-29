package com.pishgaman.phonebook.exceptions;


import org.springframework.security.core.AuthenticationException;

public class ExpiredJwtDurationException extends AuthenticationException {
    public ExpiredJwtDurationException(String message) {
        super(message);
    }
    public ExpiredJwtDurationException(String message, Throwable cause) {
        super(message, cause);
    }
}

