package com.shoxys.budgetbuddy_backend.DTOs.SavingGoal;

import com.shoxys.budgetbuddy_backend.Config.Constants;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Request object for creating or updating a saving goal.
 */
public class SavingGoalRequest {
  @NotBlank(message = "Title is required")
  @Size(max = Constants.MAX_TITLE_LENGTH, message = "Title cannot exceed " + Constants.MAX_TITLE_LENGTH + " characters")
  private String title;

  @NotNull(message = "Target amount is required")
  @DecimalMin(value = Constants.MIN_BALANCE, message = "Target amount must be at least " + Constants.MIN_BALANCE)
  @Digits(integer = Constants.MAX_BALANCE_INTEGER_DIGITS, fraction = Constants.MAX_BALANCE_FRACTION_DIGITS, message = "Target amount must be a valid monetary value with up to " + Constants.MAX_BALANCE_INTEGER_DIGITS + " integer digits and " + Constants.MAX_BALANCE_FRACTION_DIGITS + " decimal places")
  private BigDecimal target;

  @NotNull(message = "Contributed amount is required")
  @DecimalMin(value = Constants.MIN_BALANCE, message = "Contributed amount must be at least " + Constants.MIN_BALANCE)
  @Digits(integer = Constants.MAX_BALANCE_INTEGER_DIGITS, fraction = Constants.MAX_BALANCE_FRACTION_DIGITS, message = "Contributed amount must be a valid monetary value with up to " + Constants.MAX_BALANCE_INTEGER_DIGITS + " integer digits and " + Constants.MAX_BALANCE_FRACTION_DIGITS + " decimal places")
  private BigDecimal contributed;

  @NotNull(message = "Date is required")
  private LocalDate date;

  private String imageRef;

  public SavingGoalRequest() {}

  public SavingGoalRequest(String title, BigDecimal target, BigDecimal contributed, LocalDate date, String imageRef) {
    this.title = title;
    this.target = target;
    this.contributed = contributed;
    this.date = date;
    this.imageRef = imageRef;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public BigDecimal getTarget() {
    return target;
  }

  public void setTarget(BigDecimal target) {
    this.target = target;
  }

  public BigDecimal getContributed() {
    return contributed;
  }

  public void setContributed(BigDecimal contributed) {
    this.contributed = contributed;
  }

  public LocalDate getDate() {
    return date;
  }

  public void setDate(LocalDate date) {
    this.date = date;
  }

  public String getImageRef() {
    return imageRef;
  }

  public void setImageRef(String imageRef) {
    this.imageRef = imageRef;
  }
}