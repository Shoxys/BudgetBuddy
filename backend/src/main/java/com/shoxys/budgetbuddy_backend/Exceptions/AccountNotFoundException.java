package com.shoxys.budgetbuddy_backend.Exceptions;

import com.shoxys.budgetbuddy_backend.Config.Constants;

/** Exception thrown when an account is not found for a specified user. */
public class AccountNotFoundException extends RuntimeException {
  public AccountNotFoundException() {
    super(Constants.ACCOUNT_NOT_FOUND);
  }

  public AccountNotFoundException(String message) {
    super(message);
  }
}
