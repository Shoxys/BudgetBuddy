package com.shoxys.budgetbuddy_backend.DTOs.Transaction;

import com.shoxys.budgetbuddy_backend.Config.Constants;
import com.shoxys.budgetbuddy_backend.Enums.SourceType;
import com.shoxys.budgetbuddy_backend.Enums.TransactionType;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/** Request object for creating or updating a financial transaction. */
public class TransactionRequest {
  @NotNull(message = "Date is required")
  private LocalDate date;

  @NotNull(message = "Amount is required")
  @DecimalMin(
      value = Constants.MIN_BALANCE,
      message = "Amount must be at least " + Constants.MIN_BALANCE)
  @Digits(
      integer = Constants.MAX_BALANCE_INTEGER_DIGITS,
      fraction = Constants.MAX_BALANCE_FRACTION_DIGITS,
      message =
          "Amount must be a valid monetary value with up to "
              + Constants.MAX_BALANCE_INTEGER_DIGITS
              + " integer digits and "
              + Constants.MAX_BALANCE_FRACTION_DIGITS
              + " decimal places")
  private BigDecimal amount;

  @NotBlank(message = "Description is required")
  @Size(
      max = Constants.MAX_DESCRIPTION_LENGTH,
      message = "Description cannot exceed " + Constants.MAX_DESCRIPTION_LENGTH + " characters")
  private String description;

  @NotBlank(message = "Category is required")
  @Size(
      max = Constants.MAX_CATEGORY_LENGTH,
      message = "Category cannot exceed " + Constants.MAX_CATEGORY_LENGTH + " characters")
  private String category;

  @NotNull(message = "Type is required")
  private TransactionType type;

  private String merchant;

  @NotNull(message = "Balance is required")
  @DecimalMin(
      value = Constants.MIN_BALANCE,
      message = "Balance must be at least " + Constants.MIN_BALANCE)
  @Digits(
      integer = Constants.MAX_BALANCE_INTEGER_DIGITS,
      fraction = Constants.MAX_BALANCE_FRACTION_DIGITS,
      message =
          "Balance must be a valid monetary value with up to "
              + Constants.MAX_BALANCE_INTEGER_DIGITS
              + " integer digits and "
              + Constants.MAX_BALANCE_FRACTION_DIGITS
              + " decimal places")
  private BigDecimal balanceAtTransaction;

  @NotNull(message = "Source is required")
  private SourceType source;

  public TransactionRequest() {}

  public TransactionRequest(
      LocalDate date,
      BigDecimal amount,
      String description,
      String category,
      TransactionType type,
      String merchant,
      BigDecimal balanceAtTransaction,
      SourceType source) {
    this.date = date;
    this.amount = amount;
    this.description = description;
    this.category = category;
    this.type = type;
    this.merchant = merchant;
    this.balanceAtTransaction = balanceAtTransaction;
    this.source = source;
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

  public TransactionType getType() {
    return type;
  }

  public void setType(TransactionType type) {
    this.type = type;
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

  public void setBalanceAtTransaction(BigDecimal balanceAtTransaction) {
    this.balanceAtTransaction = balanceAtTransaction;
  }

  public SourceType getSource() {
    return source;
  }

  public void setSource(SourceType source) {
    this.source = source;
  }
}
