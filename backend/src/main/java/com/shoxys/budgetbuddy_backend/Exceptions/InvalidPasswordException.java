package com.shoxys.budgetbuddy_backend.Exceptions;

import com.shoxys.budgetbuddy_backend.Config.Constants;

/** Exception thrown when a password is invalid. */
public class InvalidPasswordException extends RuntimeException {
  public InvalidPasswordException() {
    super(Constants.INVALID_PASSWORD);
  }

  public InvalidPasswordException(String message) {
    super(message);
  }
}
