package com.shoxys.budgetbuddy_backend.DTOs;

import com.shoxys.budgetbuddy_backend.Enums.AccountType;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class UpdateAccountRequest {
  @NotBlank(message = "Name is required")
  private String name;

  @NotNull(message = "Type is required")
  private AccountType accountType;

  @NotNull(message = "Balance is required")
  @DecimalMin(value = "0.01", message = "Balance must be greater than 0")
  @Digits(integer = 12, fraction = 2, message = "Balance must be a valid monetary value")
  private BigDecimal balance;

  public UpdateAccountRequest(String name, AccountType accountType, BigDecimal balance) {
    this.name = name;
    this.accountType = accountType;
    this.balance = balance;
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
