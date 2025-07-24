package com.shoxys.budgetbuddy_backend.Exceptions;

import com.shoxys.budgetbuddy_backend.Config.Constants;

/** Exception thrown when an email address already exists in the system. */
public class EmailExistsException extends RuntimeException {
  public EmailExistsException() {
    super(Constants.EMAIL_EXISTS);
  }

  public EmailExistsException(String message) {
    super(message);
  }
}
