package com.shoxys.budgetbuddy_backend.Services;

import com.shoxys.budgetbuddy_backend.DTOs.Auth.AuthResponse;
import com.shoxys.budgetbuddy_backend.DTOs.Auth.LoginRequest;
import com.shoxys.budgetbuddy_backend.DTOs.Auth.RegisterRequest;
import com.shoxys.budgetbuddy_backend.Entities.User;
import com.shoxys.budgetbuddy_backend.Exceptions.*;
import com.shoxys.budgetbuddy_backend.Repo.UserRepo;
import com.shoxys.budgetbuddy_backend.Security.AppUserDetails;
import com.shoxys.budgetbuddy_backend.Security.JwtUtil;
import com.shoxys.budgetbuddy_backend.Utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service for handling user authentication and registration.
 */
@Service
public class AuthService {
  private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
  private final UserRepo userRepo;
  private final AuthenticationManager authenticationManager;
  private final JwtUtil jwtUtil;
  private final PasswordEncoder passwordEncoder;

  /**
   * Constructs an AuthService with required dependencies.
   *
   * @param authenticationManager the authentication manager for user authentication
   * @param jwtUtil              utility for JWT generation
   * @param userRepo             repository for user data access
   * @param passwordEncoder      encoder for password hashing
   */
  public AuthService(AuthenticationManager authenticationManager, JwtUtil jwtUtil,
                     UserRepo userRepo, PasswordEncoder passwordEncoder) {
    this.authenticationManager = authenticationManager;
    this.jwtUtil = jwtUtil;
    this.userRepo = userRepo;
    this.passwordEncoder = passwordEncoder;
    logger.info("Initializing AuthService");
  }

  /**
   * Authenticates a user based on login credentials and generates a JWT token.
   *
   * @param request the login request containing email and password
   * @return an AuthResponse containing the JWT token and success message
   * @throws IllegalArgumentException      if email or password is invalid
   * @throws InvalidEmailFormatException  if email format is invalid
   * @throws InvalidCredentialsException  if authentication fails
   * @throws UserNotFoundException        if user is not found after authentication
   * @throws RuntimeException             if a server error occurs
   */
  public AuthResponse authenticate(LoginRequest request) {
    logger.debug("Authenticating user: {}", request != null ? request.getEmail() : null);
    if (request == null) {
      logger.error("Login request is null");
      throw new IllegalArgumentException("Login request cannot be null");
    }
    if (Utils.nullOrEmpty(request.getEmail()) || Utils.nullOrEmpty(request.getPassword())) {
      logger.error("Missing required fields for authentication: email={}, password={}",
              request.getEmail(), request.getPassword() != null ? "****" : null);
      throw new MissingFieldException("Email and password are required");
    }
    if (!Utils.isEmail(request.getEmail())) {
      logger.error("Invalid email format: {}", request.getEmail());
      throw new InvalidEmailFormatException("Invalid email format");
    }

    Authentication authentication;
    try {
      authentication = authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
      logger.info("Authentication successful for: {}", authentication.getName());
    } catch (BadCredentialsException e) {
      logger.error("Invalid credentials for: {}", request.getEmail());
      throw new InvalidCredentialsException("Invalid email or password");
    } catch (Exception e) {
      logger.error("Authentication error for: {}: {}", request.getEmail(), e.getMessage(), e);
      throw new RuntimeException("Server error during authentication", e);
    }

    SecurityContextHolder.getContext().setAuthentication(authentication);
    String email = authentication.getName();
    User user = userRepo.findByEmail(email).orElseThrow(() -> {
      logger.error("User not found after authentication: {}", email);
      return new UserNotFoundException("User not found: " + email);
    });
    AppUserDetails userDetails = new AppUserDetails(user);
    String jwt = jwtUtil.generateToken(userDetails);
    logger.info("Generated JWT for user: {}", email);
    return new AuthResponse(jwt, "User authenticated successfully");
  }

  /**
   * Registers a new user with the provided credentials.
   *
   * @param request the registration request containing email, password, and confirm password
   * @throws IllegalArgumentException     if request fields are invalid
   * @throws InvalidEmailFormatException if email format is invalid
   * @throws PasswordMismatchException   if passwords do not match
   * @throws InvalidPasswordException    if password does not meet criteria
   * @throws EmailExistsException        if email is already registered
   */
  public void register(RegisterRequest request) {
    logger.debug("Registering user: {}", request != null ? request.getEmail() : null);
    if (request == null) {
      logger.error("Register request is null");
      throw new IllegalArgumentException("Register request cannot be null");
    }
    if (Utils.nullOrEmpty(request.getEmail()) || Utils.nullOrEmpty(request.getPassword()) ||
            Utils.nullOrEmpty(request.getConfirmPassword())) {
      logger.error("Missing required fields for registration: email={}, password={}, confirmPassword={}",
              request.getEmail(), request.getPassword() != null ? "****" : null,
              request.getConfirmPassword() != null ? "****" : null);
      throw new MissingFieldException("All fields are required");
    }
    if (!Utils.isEmail(request.getEmail())) {
      logger.error("Invalid email format: {}", request.getEmail());
      throw new InvalidEmailFormatException("Invalid email format");
    }

    if (userRepo.existsByEmail(request.getEmail())) {
      logger.error("Email already exists: {}", request.getEmail());
      throw new EmailExistsException("Email already exists");
    }

    if (!request.getPassword().equals(request.getConfirmPassword())) {
      logger.error("Passwords do not match for: {}", request.getEmail());
      throw new PasswordMismatchException("Passwords do not match");
    }
    if (!Utils.isValidPassword(request.getPassword())) {
      logger.error("Invalid password for: {}", request.getEmail());
      throw new InvalidPasswordException("Password must be at least 8 characters");
    }

    User user = new User();
    user.setEmail(request.getEmail());
    user.setHashedPassword(passwordEncoder.encode(request.getPassword()));
    userRepo.save(user);
    logger.info("Registered user: {}", request.getEmail());
  }
}