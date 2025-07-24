package com.shoxys.budgetbuddy_backend.DTOs.Dashboard;

import com.shoxys.budgetbuddy_backend.Enums.AccountType;
import java.math.BigDecimal;

public class AccountSummary {
  private long id;
  private AccountType type;
  private String name;
  private BigDecimal balance;

  /** Summary of an account's details for dashboard display. */
  public AccountSummary(long id, AccountType type, String name, BigDecimal balance) {
    this.id = id;
    this.type = type;
    this.name = name;
    this.balance = balance;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public AccountType getType() {
    return type;
  }

  public void setType(AccountType type) {
    this.type = type;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public BigDecimal getBalance() {
    return balance;
  }

  public void setBalance(BigDecimal balance) {
    this.balance = balance;
  }
}
