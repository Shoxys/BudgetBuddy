package com.shoxys.budgetbuddy_backend.DTOs.Auth;

import com.shoxys.budgetbuddy_backend.Config.Constants;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request object for updating a user's email address.
 */
public class UpdateEmailRequest {
  @Email(message = "Please provide a valid email address")
  @NotBlank(message = "Current email is required")
  @Size(max = Constants.MAX_EMAIL_LENGTH, message = "Current email cannot exceed " + Constants.MAX_EMAIL_LENGTH + " characters")
  private String currentEmail;

  @Email(message = "Please provide a valid email address")
  @NotBlank(message = "New email is required")
  @Size(max = Constants.MAX_EMAIL_LENGTH, message = "New email cannot exceed " + Constants.MAX_EMAIL_LENGTH + " characters")
  private String newEmail;

  @NotBlank(message = "Confirm password is required")
  @Size(min = Constants.MIN_PASSWORD_LENGTH, max = Constants.MAX_PASSWORD_LENGTH, message = "Confirm password must be between " + Constants.MIN_PASSWORD_LENGTH + " and " + Constants.MAX_PASSWORD_LENGTH + " characters")
  private String confirmPassword;

  public UpdateEmailRequest(String currentEmail, String newEmail, String confirmPassword) {
    this.currentEmail = currentEmail;
    this.newEmail = newEmail;
    this.confirmPassword = confirmPassword;
  }

  public UpdateEmailRequest() {
  }

  public String getCurrentEmail() {
    return currentEmail;
  }

  public void setCurrentEmail(String currentEmail) {
    this.currentEmail = currentEmail;
  }

  public String getNewEmail() {
    return newEmail;
  }

  public void setNewEmail(String newEmail) {
    this.newEmail = newEmail;
  }

  public String getConfirmPassword() {
    return confirmPassword;
  }

  public void setConfirmPassword(String confirmPassword) {
    this.confirmPassword = confirmPassword;
  }
}