package com.shoxys.budgetbuddy_backend.Exceptions;

import com.shoxys.budgetbuddy_backend.Config.Constants;

/** Exception thrown when passwords do not match during verification. */
public class PasswordMismatchException extends RuntimeException {
  public PasswordMismatchException() {
    super(Constants.PASSWORD_MISMATCH);
  }

  public PasswordMismatchException(String message) {
    super(message);
  }
}
