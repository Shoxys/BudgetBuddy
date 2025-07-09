package com.shoxys.budgetbuddy_backend.Exceptions;


public class EmailExistsException extends RuntimeException {
    public EmailExistsException() {
        super("Email already exists");
    }

    public EmailExistsException(String message) {
        super(message);
    }
}
