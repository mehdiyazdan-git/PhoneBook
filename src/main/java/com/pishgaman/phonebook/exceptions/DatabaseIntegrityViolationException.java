package com.pishgaman.phonebook.exceptions;

import org.springframework.dao.DataIntegrityViolationException;

public class DatabaseIntegrityViolationException extends DataIntegrityViolationException {

    public DatabaseIntegrityViolationException(String message) {
        super(message);
    }

    public DatabaseIntegrityViolationException(String message, Throwable cause) {
        super(message, cause);
    }
}
