package com.shoxys.budgetbuddy_backend.Services;

import com.shoxys.budgetbuddy_backend.DTOs.Auth.AuthResponse;
import com.shoxys.budgetbuddy_backend.DTOs.Auth.ChangePasswordRequest;
import com.shoxys.budgetbuddy_backend.DTOs.Auth.UpdateEmailRequest;
import com.shoxys.budgetbuddy_backend.Entities.User;
import com.shoxys.budgetbuddy_backend.Exceptions.EmailExistsException;
import com.shoxys.budgetbuddy_backend.Exceptions.UserNotFoundException;
import com.shoxys.budgetbuddy_backend.Repo.UserRepo;
import com.shoxys.budgetbuddy_backend.Security.AppUserDetails;
import com.shoxys.budgetbuddy_backend.Security.JwtUtil;
import com.shoxys.budgetbuddy_backend.Utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
  private final UserRepo userRepo;
  private final AuthService authService;
  private final JwtUtil jwtUtil;
  private final PasswordEncoder passwordEncoder;

  @Autowired
  public UserService(
      UserRepo userRepo,
      AuthService authService,
      JwtUtil jwtUtil,
      PasswordEncoder passwordEncoder) {
    this.userRepo = userRepo;
    this.authService = authService;
    this.jwtUtil = jwtUtil;
    this.passwordEncoder = passwordEncoder;
  }

  public long getUserIdByEmail(String email) {
    return userRepo.getUserIdByEmail(email);
  }

  public User getUserByEmail(String email) {
    return userRepo
        .getUserByEmail(email)
        .orElseThrow(() -> new UserNotFoundException("User not found"));
  }

  public void changePassword(String email, ChangePasswordRequest request) {
    User user =
        userRepo
            .getUserByEmail(email)
            .orElseThrow(() -> new UserNotFoundException("Incorrect account details"));

    if (!passwordEncoder.matches(request.getCurrentPassword(), user.getHashedPassword())) {
      throw new SecurityException("Incorrect password");
    }

    if (!request.getNewPassword().equals(request.getConfirmPassword())) {
      throw new SecurityException("Passwords do not match");
    }

    user.setHashedPassword(passwordEncoder.encode(request.getNewPassword()));
    userRepo.save(user);
  }

  public AuthResponse updateEmail(String email, UpdateEmailRequest request) {
    User user =
        userRepo
            .getUserByEmail(email)
            .orElseThrow(() -> new UserNotFoundException("Incorrect account details"));

    if (!passwordEncoder.matches(request.getConfirmPassword(), user.getHashedPassword())) {
      throw new SecurityException("Incorrect account details");
    }

    if (!Utils.isEmail(request.getNewEmail())) {
      throw new IllegalArgumentException("Please enter valid email address");
    }

    if (request.getCurrentEmail().equals(request.getNewEmail())) {
      throw new EmailExistsException("Email is the same as current");
    }

    if (userRepo.existsByEmail(request.getNewEmail())) {
      throw new EmailExistsException("Email already exists");
    }

    user.setEmail(request.getNewEmail());
    userRepo.save(user);

    AppUserDetails userDetails = new AppUserDetails(user);
    String newToken = jwtUtil.generateToken(userDetails);
    return new AuthResponse(newToken, "Email updated successfully");
  }

  public void deleteAccount(String email) {
    User user =
        userRepo
            .getUserByEmail(email)
            .orElseThrow(() -> new UserNotFoundException("User doesn't exist"));
    userRepo.delete(user);
  }
}
