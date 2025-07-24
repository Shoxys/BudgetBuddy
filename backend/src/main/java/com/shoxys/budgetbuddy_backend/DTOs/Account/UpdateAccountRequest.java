package com.shoxys.budgetbuddy_backend.DTOs.Account;

import com.shoxys.budgetbuddy_backend.Config.Constants;
import com.shoxys.budgetbuddy_backend.Enums.AccountType;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/**
 * Data transfer object for updating an account's details, including name, type, and balance.
 */
public class UpdateAccountRequest {

  @NotNull(message = "Account ID is required")
  private Long id;

  @NotBlank(message = "Account name is required")
  @Size(max = Constants.MAX_ACCOUNT_NAME_LENGTH, message = "Account name cannot exceed " + Constants.MAX_ACCOUNT_NAME_LENGTH + " characters")
  private String name;

  @NotNull(message = "Account type is required")
  private AccountType accountType;

  @NotNull(message = "Balance is required")
  @DecimalMin(value = Constants.MIN_BALANCE, message = "Balance must be at least " + Constants.MIN_BALANCE)
  @Digits(integer = Constants.MAX_BALANCE_INTEGER_DIGITS, fraction = Constants.MAX_BALANCE_FRACTION_DIGITS, message = "Balance must be a valid monetary value with up to " + Constants.MAX_BALANCE_INTEGER_DIGITS + " integer digits and " + Constants.MAX_BALANCE_FRACTION_DIGITS + " decimal places")
  private BigDecimal balance;

  public UpdateAccountRequest(Long id, String name, AccountType accountType, BigDecimal balance) {
    this.id = id;
    this.name = name;
    this.accountType = accountType;
    this.balance = balance;
  }

  public UpdateAccountRequest() {
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public AccountType getAccountType() {
    return accountType;
  }

  public void setAccountType(AccountType accountType) {
    this.accountType = accountType;
  }

  public BigDecimal getBalance() {
    return balance;
  }

  public void setBalance(BigDecimal balance) {
    this.balance = balance;
  }
}