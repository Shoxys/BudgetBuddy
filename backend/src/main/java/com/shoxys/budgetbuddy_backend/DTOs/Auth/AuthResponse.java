package com.shoxys.budgetbuddy_backend.DTOs.Auth;

/**
 * Response object for authentication operations, containing a JWT token and a message.
 */
public class AuthResponse {
  private String token;
  private String message;

  public AuthResponse(String token, String message) {
    this.token = token;
    this.message = message;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }
}