package com.shoxys.budgetbuddy_backend.DTOs.Auth;

import com.shoxys.budgetbuddy_backend.Config.Constants;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Request object for user registration with email, password, and confirmation password. */
public class RegisterRequest {
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

  @NotBlank(message = "Confirm password is required")
  @Size(
      min = Constants.MIN_PASSWORD_LENGTH,
      max = Constants.MAX_PASSWORD_LENGTH,
      message =
          "Confirm password must be between "
              + Constants.MIN_PASSWORD_LENGTH
              + " and "
              + Constants.MAX_PASSWORD_LENGTH
              + " characters")
  private String confirmPassword;

  public RegisterRequest(String email, String password, String confirmPassword) {
    this.email = email;
    this.password = password;
    this.confirmPassword = confirmPassword;
  }

  public RegisterRequest() {}

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

  public String getConfirmPassword() {
    return confirmPassword;
  }

  public void setConfirmPassword(String confirmPassword) {
    this.confirmPassword = confirmPassword;
  }
}
