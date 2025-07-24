package com.shoxys.budgetbuddy_backend.Controllers;

import com.shoxys.budgetbuddy_backend.Config.Constants;
import com.shoxys.budgetbuddy_backend.DTOs.Account.UpdateAccountRequest;
import com.shoxys.budgetbuddy_backend.Enums.AccountType;
import com.shoxys.budgetbuddy_backend.Security.AppUserDetails;
import com.shoxys.budgetbuddy_backend.Services.AccountService;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/** Handles HTTP requests for managing user accounts. */
@RestController
@RequestMapping(Constants.ACCOUNT_ENDPOINT)
public class AccountController {

  private static final Logger logger = LoggerFactory.getLogger(AccountController.class);
  private final AccountService accountService;

  public AccountController(AccountService accountService) {
    this.accountService = accountService;
  }

  /**
   * Updates an account's balance and details for the authenticated user.
   *
   * @param userDetails the authenticated user's details
   * @param request the account update request
   * @return a response indicating success or unauthorized status
   */
  @PostMapping("/update")
  public ResponseEntity<String> updateAccount(
      @AuthenticationPrincipal AppUserDetails userDetails,
      @Valid @RequestBody UpdateAccountRequest request) {
    if (userDetails == null) {
      logger.warn("Unauthorized account update attempt");
      return ResponseEntity.status(401).body("User is not authenticated");
    }
    String username = userDetails.getUsername();
    logger.info("Updating account for user: {}, account ID: {}", username, request.getId());
    accountService.upsertAccountBalance(
        username,
        request.getId(),
        request.getName(),
        request.getAccountType(),
        request.getBalance());
    logger.info("Account updated for user: {}", username);
    return ResponseEntity.ok("Account updated successfully");
  }

  /**
   * Retrieves the balance for a specific account of the authenticated user.
   *
   * @param userDetails the authenticated user's details
   * @param name the account name
   * @param accountType the type of account (e.g., CHECKING, SAVINGS)
   * @return the account balance or zero if not found
   */
  @GetMapping("/balance")
  public ResponseEntity<BigDecimal> getAccountBalance(
      @AuthenticationPrincipal AppUserDetails userDetails,
      @RequestParam String name,
      @RequestParam AccountType accountType) {
    if (userDetails == null) {
      logger.warn("Unauthorized balance request");
      return ResponseEntity.status(401).body(null);
    }
    String username = userDetails.getUsername();
    logger.info(
        "Fetching balance for user: {}, account: {}, type: {}", username, name, accountType);
    try {
      BigDecimal balance = accountService.getAccountBalance(username, name, accountType);
      if (balance != null) {
        logger.info("Balance retrieved for user: {}, account: {}", username, name);
        return ResponseEntity.ok(balance);
      }
      logger.info(
          "No balance found for user: {}, account: {}, type: {}. Returning zero",
          username,
          name,
          accountType);
      return ResponseEntity.ok(BigDecimal.ZERO);
    } catch (Exception e) {
      logger.error(
          "Failed to retrieve balance for user: {}, account: {}, type: {}",
          username,
          name,
          accountType,
          e);
      throw e;
    }
  }
}
