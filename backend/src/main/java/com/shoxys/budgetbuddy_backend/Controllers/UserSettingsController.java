package com.shoxys.budgetbuddy_backend.Controllers;

import com.shoxys.budgetbuddy_backend.Config.Constants;
import com.shoxys.budgetbuddy_backend.DTOs.Auth.AuthResponse;
import com.shoxys.budgetbuddy_backend.DTOs.Auth.ChangePasswordRequest;
import com.shoxys.budgetbuddy_backend.DTOs.Auth.UpdateEmailRequest;
import com.shoxys.budgetbuddy_backend.Entities.User;
import com.shoxys.budgetbuddy_backend.Security.AppUserDetails;
import com.shoxys.budgetbuddy_backend.Services.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;

/**
 * Handles HTTP requests for managing user settings.
 */
@RestController
@RequestMapping(Constants.SETTINGS_ENDPOINT)
public class UserSettingsController {
  private static final Logger logger = LoggerFactory.getLogger(UserSettingsController.class);
  private final UserService userService;

  public UserSettingsController(UserService userService) {
    this.userService = userService;
  }

  /**
   * Retrieves the current email of the authenticated user.
   *
   * @param userDetails the authenticated user's details
   * @return the user's email
   */
  @GetMapping("/current-email")
  public ResponseEntity<String> getCurrentEmail(@AuthenticationPrincipal AppUserDetails userDetails) {
    String username = validateUserDetails(userDetails);
    logger.info("Fetching current email for user: {}", username);
    return ResponseEntity.ok(username);
  }

  /**
   * Updates the password for the authenticated user.
   *
   * @param userDetails the authenticated user's details
   * @param request the password change request
   * @return a response indicating success
   */
  @PutMapping("/change-password")
  public ResponseEntity<String> updatePassword(
          @AuthenticationPrincipal AppUserDetails userDetails,
          @Valid @RequestBody ChangePasswordRequest request) {
    String username = validateUserDetails(userDetails);
    logger.info("Updating password for user: {}", username);
    userService.changePassword(username, request);
    logger.info("Password updated for user: {}", username);
    return ResponseEntity.ok("Password changed successfully");
  }

  /**
   * Updates the email for the authenticated user and refreshes the JWT cookie.
   *
   * @param userDetails the authenticated user's details
   * @param request the email update request
   * @param response the HTTP response to set the JWT cookie
   * @return the authentication response with the new token
   */
  @PutMapping("/update-email")
  public ResponseEntity<AuthResponse> updateEmail(
          @AuthenticationPrincipal AppUserDetails userDetails,
          @Valid @RequestBody UpdateEmailRequest request,
          HttpServletResponse response) {
    String username = validateUserDetails(userDetails);
    logger.info("Updating email for user: {}", username);
    AuthResponse authResponse = userService.updateEmail(username, request);
    User updatedUser = userService.getUserByEmail(request.getNewEmail());
    AppUserDetails updatedUserDetails = new AppUserDetails(updatedUser);
    SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(updatedUserDetails, null, updatedUserDetails.getAuthorities())
    );
    addJwtCookie(response, authResponse.getToken());
    logger.info("Email updated for user: {}, new email: {}", username, request.getNewEmail());
    return ResponseEntity.ok(authResponse);
  }

  /**
   * Deletes the authenticated user's account and clears the JWT cookie.
   *
   * @param userDetails the authenticated user's details
   * @param response the HTTP response to clear the JWT cookie
   * @return a response indicating success
   */
  @DeleteMapping("/delete-account")
  public ResponseEntity<String> deleteAccount(
          @AuthenticationPrincipal AppUserDetails userDetails, HttpServletResponse response) {
    String username = validateUserDetails(userDetails);
    logger.info("Deleting account for user: {}", username);
    userService.deleteAccount(username);
    clearJwtCookie(response);
    logger.info("Account deleted for user: {}", username);
    return ResponseEntity.ok("Account deleted successfully");
  }

  private String validateUserDetails(AppUserDetails userDetails) {
    if (userDetails == null) {
      logger.warn("Unauthorized settings request");
      throw new IllegalStateException("User is not authenticated");
    }
    return userDetails.getUsername();
  }

  private void addJwtCookie(HttpServletResponse response, String jwtToken) {
    Cookie cookie = new Cookie(Constants.JWT_COOKIE_NAME, jwtToken);
    cookie.setPath(Constants.COOKIE_PATH);
    cookie.setHttpOnly(Constants.COOKIE_HTTP_ONLY);
    cookie.setSecure(Constants.COOKIE_SECURE);
    cookie.setMaxAge((int) Constants.COOKIE_MAX_AGE_SECONDS);
    cookie.setAttribute("SameSite", Constants.COOKIE_SAME_SITE);
    response.addCookie(cookie);
  }

  private void clearJwtCookie(HttpServletResponse response) {
    Cookie cookie = new Cookie(Constants.JWT_COOKIE_NAME, null);
    cookie.setPath(Constants.COOKIE_PATH);
    cookie.setHttpOnly(Constants.COOKIE_HTTP_ONLY);
    cookie.setSecure(Constants.COOKIE_SECURE);
    cookie.setMaxAge(0);
    cookie.setAttribute("SameSite", Constants.COOKIE_SAME_SITE);
    response.addCookie(cookie);
  }
}