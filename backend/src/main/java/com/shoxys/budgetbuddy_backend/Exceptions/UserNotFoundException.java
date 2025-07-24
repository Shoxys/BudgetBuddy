package com.shoxys.budgetbuddy_backend.Exceptions;

import com.shoxys.budgetbuddy_backend.Utils.Utils;

/**
 * Exception thrown when a user is not found by ID or email.
 */
public class UserNotFoundException extends RuntimeException {

  /**
   * Constructs an exception with a message indicating the user ID that was not found.
   *
   * @param userId the ID of the user that was not found
   */
  public UserNotFoundException(Long userId) {
    super("User with ID " + userId + " not found.");
  }

  /**
   * Constructs an exception with a message indicating the email or custom input that was not found.
   * If the input is a valid email format, the message specifies the email; otherwise, it uses the input as-is.
   *
   * @param input the email or custom message
   */
  public UserNotFoundException(String input) {
    super(Utils.isEmail(input) ? "User with email " + input + " not found." : input);
  }
}