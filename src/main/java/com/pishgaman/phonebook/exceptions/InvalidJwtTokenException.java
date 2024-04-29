package com.pishgaman.phonebook.exceptions;

import org.springframework.security.core.AuthenticationException;

public class InvalidJwtTokenException extends AuthenticationException {
    public InvalidJwtTokenException(String msg) {
        super(msg);
    }

    public InvalidJwtTokenException(String msg, Throwable t) {

        super(msg, t);
    }
}
