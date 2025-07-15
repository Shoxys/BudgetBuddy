package com.shoxys.budgetbuddy_backend.Exceptions;

public class AccountNotFoundException extends RuntimeException {
  public AccountNotFoundException() {
    super("Account not found for specified user");
  }

  public AccountNotFoundException(String message) {
    super(message);
  }
}
