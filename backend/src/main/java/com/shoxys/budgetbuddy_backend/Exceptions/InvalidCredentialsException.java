package com.shoxys.budgetbuddy_backend.Exceptions;

import com.shoxys.budgetbuddy_backend.Config.Constants;

/** Exception thrown when provided credentials are invalid. */
public class InvalidCredentialsException extends RuntimeException {
  public InvalidCredentialsException(String message) {
    super(message);
  }

  public InvalidCredentialsException() {
    super(Constants.INVALID_CREDENTIALS);
  }
}
