package com.pishgaman.phonebook.exceptions;

public class ValidTokenNotFoundInDatabase extends RuntimeException {
    public ValidTokenNotFoundInDatabase(String message){
        super(message);
    }

    public ValidTokenNotFoundInDatabase(String message, Throwable cause){
        super(message, cause);
    }
}
