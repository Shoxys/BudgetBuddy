package com.shoxys.budgetbuddy_backend.DTOs.Auth;

import com.shoxys.budgetbuddy_backend.Config.Constants;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Request object for user login with email and password. */
public class LoginRequest {
  @Email(message = "Please provide a valid email address")
  @NotBlank(message = "Email is required")
  @Size(
      max = Constants.MAX_EMAIL_LENGTH,
      message = "Email cannot exceed " + Constants.MAX_EMAIL_LENGTH + " characters")
  private String email;

  @NotBlank(message = "Password is required")
  @Size(
      min = Constants.MIN_PASSWORD_LENGTH,
      max = Constants.MAX_PASSWORD_LENGTH,
      message =
          "Password must be between "
              + Constants.MIN_PASSWORD_LENGTH
              + " and "
              + Constants.MAX_PASSWORD_LENGTH
              + " characters")
  private String password;

  public LoginRequest() {}

  public LoginRequest(String email, String password) {
    this.email = email;
    this.password = password;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}
