package com.shoxys.budgetbuddy_backend.Exceptions;

/**
 * Exception thrown when an email address has an invalid format.
 */
public class InvalidEmailFormatException extends RuntimeException {
    public InvalidEmailFormatException(String message) {
        super(message);
    }
}