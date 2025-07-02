package com.shoxys.budgetbuddy_backend.DTOs;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class UpdateEmailRequest {
    @NotBlank
    @Email
    private String currentEmail;
    @NotBlank
    @Email
    private String newEmail;
    @NotBlank
    private String confirmPassword;

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
