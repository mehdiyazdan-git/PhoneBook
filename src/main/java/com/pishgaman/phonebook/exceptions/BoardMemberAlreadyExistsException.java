package com.pishgaman.phonebook.exceptions;

public class BoardMemberAlreadyExistsException extends RuntimeException {

    public BoardMemberAlreadyExistsException(String message) {
        super(message);
    }
}
