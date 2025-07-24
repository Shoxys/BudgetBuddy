package com.shoxys.budgetbuddy_backend.Entities;

import com.shoxys.budgetbuddy_backend.Config.Constants;
import com.shoxys.budgetbuddy_backend.Enums.AccountType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/**
 * Entity representing a financial account owned by a user.
 */
@Entity
@Table(name = "accounts")
public class Account {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank(message = "Name is required")
  @Size(max = Constants.MAX_ACCOUNT_NAME_LENGTH, message = "Name cannot exceed " + Constants.MAX_ACCOUNT_NAME_LENGTH + " characters")
  @Column(nullable = false)
  private String name;

  @NotNull(message = "Account type is required")
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private AccountType type;

  @Column(nullable = true)
  private Integer accountNo;

  @NotNull(message = "Balance is required")
  @DecimalMin(value = Constants.MIN_BALANCE, message = "Balance must be at least " + Constants.MIN_BALANCE)
  @Digits(integer = Constants.MAX_BALANCE_INTEGER_DIGITS, fraction = Constants.MAX_BALANCE_FRACTION_DIGITS, message = "Balance must be a valid monetary value with up to " + Constants.MAX_BALANCE_INTEGER_DIGITS + " integer digits and " + Constants.MAX_BALANCE_FRACTION_DIGITS + " decimal places")
  @Column(nullable = false, precision = Constants.MAX_BALANCE_INTEGER_DIGITS, scale = Constants.MAX_BALANCE_FRACTION_DIGITS)
  private BigDecimal balance;

  @Column(nullable = false)
  private boolean isManual;

  @ManyToOne
  @JoinColumn(name = "user_id", referencedColumnName = "id")
  private User user;

  public Account() {
    this.balance = BigDecimal.ZERO;
  }

  public Account(
          String name,
          AccountType type,
          Integer accountNo,
          BigDecimal balance,
          boolean isManual,
          User user) {
    this.name = name;
    this.type = type;
    this.accountNo = accountNo;
    this.balance = balance;
    this.isManual = isManual;
    this.user = user;
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

  public AccountType getType() {
    return type;
  }

  public void setType(AccountType type) {
    this.type = type;
  }

  public Integer getAccountNo() {
    return accountNo;
  }

  public void setAccountNo(Integer accountNo) {
    this.accountNo = accountNo;
  }

  public BigDecimal getBalance() {
    return balance;
  }

  public void setBalance(BigDecimal balance) {
    this.balance = balance;
  }

  public boolean isManual() {
    return isManual;
  }

  public void setManual(boolean isManual) {
    this.isManual = isManual;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }
}