package com.shoxys.budgetbuddy_backend.Entities;

import com.shoxys.budgetbuddy_backend.Config.Constants;
import com.shoxys.budgetbuddy_backend.Enums.SourceType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entity representing a financial transaction linked to an account and user.
 */
@Entity
@Table(name = "transactions")
public class Transaction {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull(message = "Date is required")
  @Column(nullable = false)
  private LocalDate date;

  @NotNull(message = "Amount is required")
  @DecimalMin(value = Constants.MIN_BALANCE, message = "Amount must be at least " + Constants.MIN_BALANCE)
  @Digits(integer = Constants.MAX_BALANCE_INTEGER_DIGITS, fraction = Constants.MAX_BALANCE_FRACTION_DIGITS, message = "Amount must be a valid monetary value with up to " + Constants.MAX_BALANCE_INTEGER_DIGITS + " integer digits and " + Constants.MAX_BALANCE_FRACTION_DIGITS + " decimal places")
  @Column(nullable = false, precision = Constants.MAX_BALANCE_INTEGER_DIGITS, scale = Constants.MAX_BALANCE_FRACTION_DIGITS)
  private BigDecimal amount;

  @NotBlank(message = "Description is required")
  @Size(max = Constants.MAX_DESCRIPTION_LENGTH, message = "Description cannot exceed " + Constants.MAX_DESCRIPTION_LENGTH + " characters")
  @Column(nullable = false)
  private String description;

  @NotBlank(message = "Category is required")
  @Size(max = Constants.MAX_CATEGORY_LENGTH, message = "Category cannot exceed " + Constants.MAX_CATEGORY_LENGTH + " characters")
  @Column(nullable = false)
  private String category;

  @Size(max = Constants.MAX_ACCOUNT_NAME_LENGTH, message = "Merchant cannot exceed " + Constants.MAX_ACCOUNT_NAME_LENGTH + " characters")
  @Column(nullable = true)
  private String merchant;

  @NotNull(message = "Balance is required")
  @DecimalMin(value = Constants.MIN_BALANCE, message = "Balance must be at least " + Constants.MIN_BALANCE)
  @Digits(integer = Constants.MAX_BALANCE_INTEGER_DIGITS, fraction = Constants.MAX_BALANCE_FRACTION_DIGITS, message = "Balance must be a valid monetary value with up to " + Constants.MAX_BALANCE_INTEGER_DIGITS + " integer digits and " + Constants.MAX_BALANCE_FRACTION_DIGITS + " decimal places")
  @Column(nullable = false, precision = Constants.MAX_BALANCE_INTEGER_DIGITS, scale = Constants.MAX_BALANCE_FRACTION_DIGITS)
  private BigDecimal balanceAtTransaction;

  @NotNull(message = "Source is required")
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private SourceType source;

  @ManyToOne
  @JoinColumn(name = "account_id", referencedColumnName = "id")
  private Account account;

  @ManyToOne
  @JoinColumn(name = "user_id", referencedColumnName = "id")
  private User user;

  public Transaction() {}

  public Transaction(
          LocalDate date,
          BigDecimal amount,
          String description,
          String category,
          String merchant,
          BigDecimal balanceAtTransaction,
          SourceType source,
          Account account,
          User user) {
    this.date = date;
    this.amount = amount;
    this.description = description;
    this.category = category;
    this.merchant = merchant;
    this.balanceAtTransaction = balanceAtTransaction;
    this.source = source;
    this.account = account;
    this.user = user;
  }

  public Transaction(Transaction other) {
    this.id = other.id;
    this.date = other.date;
    this.amount = other.amount;
    this.description = other.description;
    this.category = other.category;
    this.merchant = other.merchant;
    this.balanceAtTransaction = other.balanceAtTransaction;
    this.source = other.source;
    this.account = other.account;
    this.user = other.user;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public LocalDate getDate() {
    return date;
  }

  public void setDate(LocalDate date) {
    this.date = date;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public String getMerchant() {
    return merchant;
  }

  public void setMerchant(String merchant) {
    this.merchant = merchant;
  }

  public BigDecimal getBalanceAtTransaction() {
    return balanceAtTransaction;
  }

  /**
   * Sets the balance at the time of the transaction.
   * TODO: Implement logic to validate or compute the balance (e.g., check against account balance).
   */
  public void setBalanceAtTransaction(BigDecimal balanceAtTransaction) {
    this.balanceAtTransaction = balanceAtTransaction;
  }

  public SourceType getSource() {
    return source;
  }

  public void setSource(SourceType source) {
    this.source = source;
  }

  public Account getAccount() {
    return account;
  }

  public void setAccount(Account account) {
    this.account = account;
  }
}