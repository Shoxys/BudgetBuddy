package com.shoxys.budgetbuddy_backend.Controllers;

import com.shoxys.budgetbuddy_backend.Config.Constants;
import com.shoxys.budgetbuddy_backend.DTOs.Auth.AuthResponse;
import com.shoxys.budgetbuddy_backend.DTOs.Auth.LoginRequest;
import com.shoxys.budgetbuddy_backend.DTOs.Auth.RegisterRequest;
import com.shoxys.budgetbuddy_backend.DTOs.Auth.RegisterResponse;
import com.shoxys.budgetbuddy_backend.Exceptions.*;
import com.shoxys.budgetbuddy_backend.Services.AuthService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;

/**
 * Handles authentication-related HTTP requests for user registration, login, and logout.
 */
@RestController
@RequestMapping(Constants.AUTH_ENDPOINT)
public class AuthController {
  private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  /**
   * Registers a new user and automatically logs them in, setting a JWT cookie.
   *
   * @param request  the registration request containing email and password
   * @param response the HTTP response to set the JWT cookie
   * @return a response indicating success or failure
   */
  @PostMapping("/signup")
  public ResponseEntity<RegisterResponse> register(
          @Valid @RequestBody RegisterRequest request, HttpServletResponse response) {
    logger.info("Register request for email: {}", request.getEmail());
    try {
      authService.register(request);
      LoginRequest loginRequest = new LoginRequest();
      loginRequest.setEmail(request.getEmail());
      loginRequest.setPassword(request.getPassword());
      String jwtToken = authService.authenticate(loginRequest).getToken();
      addJwtCookie(response, jwtToken);
      logger.info("User registered and logged in successfully: {}", request.getEmail());
      return ResponseEntity.ok(new RegisterResponse("User registered successfully", true));
    } catch (InvalidEmailFormatException | MissingFieldException | PasswordMismatchException |
             InvalidPasswordException | EmailExistsException e) {
      logger.error("Registration failed for email {}: {}", request.getEmail(), e.getMessage());
      return ResponseEntity.badRequest().body(new RegisterResponse(e.getMessage(), false));
    } catch (Exception e) {
      logger.error("Server error during registration for email {}: {}", request.getEmail(), e.getMessage());
      return ResponseEntity.internalServerError().body(new RegisterResponse("Server error occurred", false));
    }
  }

  /**
   * Authenticates a user and sets a JWT cookie.
   *
   * @param request  the login request containing email and password
   * @param response the HTTP response to set the JWT cookie
   * @return a response with the JWT token or error message
   */
  @PostMapping("/login")
  public ResponseEntity<AuthResponse> login(
          @Valid @RequestBody LoginRequest request, HttpServletResponse response) {
    logger.info("Login request for email: {}", request.getEmail());
    try {
      String jwtToken = authService.authenticate(request).getToken();
      addJwtCookie(response, jwtToken);
      logger.info("User logged in successfully: {}", request.getEmail());
      return ResponseEntity.ok(new AuthResponse(jwtToken, "Login successful"));
    } catch (InvalidEmailFormatException | MissingFieldException | InvalidCredentialsException e) {
      logger.error("Login failed for email {}: {}", request.getEmail(), e.getMessage());
      return ResponseEntity.badRequest().body(new AuthResponse(null, e.getMessage()));
    } catch (Exception e) {
      logger.error("Server error during login for email {}: {}", request.getEmail(), e.getMessage());
      return ResponseEntity.internalServerError().body(new AuthResponse(null, "Server error occurred"));
    }
  }

  /**
   * Logs out a user by clearing the JWT cookie.
   *
   * @param response the HTTP response to clear the JWT cookie
   * @return a response indicating logout success
   */
  @PostMapping("/logout")
  public ResponseEntity<String> logout(HttpServletResponse response) {
    logger.info("Logout request received");
    ResponseCookie cookie = ResponseCookie.from(Constants.JWT_COOKIE_NAME, null)
            .httpOnly(Constants.COOKIE_HTTP_ONLY)
            .secure(Constants.COOKIE_SECURE)
            .path(Constants.COOKIE_PATH)
            .maxAge(0)
            .sameSite(Constants.COOKIE_SAME_SITE)
            .build();
    response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    logger.info("User logged out successfully");
    return ResponseEntity.ok("Logged out successfully");
  }

  private void addJwtCookie(HttpServletResponse response, String jwtToken) {
    ResponseCookie cookie = ResponseCookie.from(Constants.JWT_COOKIE_NAME, jwtToken)
            .httpOnly(Constants.COOKIE_HTTP_ONLY)
            .secure(Constants.COOKIE_SECURE)
            .path(Constants.COOKIE_PATH)
            .maxAge(Constants.COOKIE_MAX_AGE_SECONDS)
            .sameSite(Constants.COOKIE_SAME_SITE)
            .build();
    response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
  }
}
