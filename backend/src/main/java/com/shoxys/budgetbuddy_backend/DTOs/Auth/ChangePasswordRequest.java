package com.shoxys.budgetbuddy_backend.DTOs.Auth;

import com.shoxys.budgetbuddy_backend.Config.Constants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Request object for changing a user's password. */
public class ChangePasswordRequest {
  @NotBlank(message = "Current password is required")
  @Size(
      min = Constants.MIN_PASSWORD_LENGTH,
      max = Constants.MAX_PASSWORD_LENGTH,
      message =
          "Current password must be between "
              + Constants.MIN_PASSWORD_LENGTH
              + " and "
              + Constants.MAX_PASSWORD_LENGTH
              + " characters")
  private String currentPassword;

  @NotBlank(message = "New password is required")
  @Size(
      min = Constants.MIN_PASSWORD_LENGTH,
      max = Constants.MAX_PASSWORD_LENGTH,
      message =
          "New password must be between "
              + Constants.MIN_PASSWORD_LENGTH
              + " and "
              + Constants.MAX_PASSWORD_LENGTH
              + " characters")
  private String newPassword;

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

  public ChangePasswordRequest(String currentPassword, String newPassword, String confirmPassword) {
    this.currentPassword = currentPassword;
    this.newPassword = newPassword;
    this.confirmPassword = confirmPassword;
  }

  public ChangePasswordRequest() {}

  public String getCurrentPassword() {
    return currentPassword;
  }

  public void setCurrentPassword(String currentPassword) {
    this.currentPassword = currentPassword;
  }

  public String getNewPassword() {
    return newPassword;
  }

  public void setNewPassword(String newPassword) {
    this.newPassword = newPassword;
  }

  public String getConfirmPassword() {
    return confirmPassword;
  }

  public void setConfirmPassword(String confirmPassword) {
    this.confirmPassword = confirmPassword;
  }
}
