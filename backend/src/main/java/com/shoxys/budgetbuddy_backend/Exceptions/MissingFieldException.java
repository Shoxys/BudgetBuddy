package com.shoxys.budgetbuddy_backend.Exceptions;

import com.shoxys.budgetbuddy_backend.Config.Constants;

/**
 * Exception thrown when a required field is missing in a request.
 */
public class MissingFieldException extends RuntimeException {
    public MissingFieldException() {
        super(Constants.MISSING_FIELD);
    }

    public MissingFieldException(String message) {
        super(message);
    }
}