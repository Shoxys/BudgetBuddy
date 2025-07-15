package com.shoxys.budgetbuddy_backend.Exceptions;

import com.shoxys.budgetbuddy_backend.Utils;

public class UserNotFoundException extends RuntimeException {
  public UserNotFoundException(Long userId) {
    super("User with ID " + userId + " not found.");
  }

  public UserNotFoundException(String input) {
    super(Utils.isEmail(input) ? "User with email " + input + " not found." : input);
  }
}
