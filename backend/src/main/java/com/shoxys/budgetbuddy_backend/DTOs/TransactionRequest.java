package com.shoxys.budgetbuddy_backend.DTOs;

import com.shoxys.budgetbuddy_backend.Enums.SourceType;
import com.shoxys.budgetbuddy_backend.Enums.TransactionType;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public class TransactionRequest {

  @NotNull(message = "Date is required")
  private LocalDate date;

  @NotNull(message = "Amount is required")
  @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
  @Digits(integer = 12, fraction = 2, message = "Amount must be a valid monetary value")
  private BigDecimal amount;

  @NotBlank(message = "Description is required")
  private String description;

  @NotBlank(message = "category is required")
  private String category;

  @NotNull(message = "type is required")
  private TransactionType type;

  private String merchant;

  @NotNull(message = "Balance is required")
  @DecimalMin(value = "0.01", message = "Balance must be greater than 0")
  @Digits(integer = 12, fraction = 2, message = "Balance must be a valid monetary value")
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

  // TODO: Add logic for this

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
