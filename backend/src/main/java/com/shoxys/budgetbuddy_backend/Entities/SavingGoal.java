package com.shoxys.budgetbuddy_backend.Entities;

import com.shoxys.budgetbuddy_backend.Config.Constants;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/** Entity representing a user's saving goal linked to an account. */
@Entity
@Table(name = "savingGoals")
public class SavingGoal {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank(message = "Title is required")
  @Size(
      max = Constants.MAX_TITLE_LENGTH,
      message = "Title cannot exceed " + Constants.MAX_TITLE_LENGTH + " characters")
  @Column(nullable = false)
  private String title;

  @NotNull(message = "Target amount is required")
  @DecimalMin(
      value = Constants.MIN_BALANCE,
      message = "Target amount must be at least " + Constants.MIN_BALANCE)
  @Digits(
      integer = Constants.MAX_BALANCE_INTEGER_DIGITS,
      fraction = Constants.MAX_BALANCE_FRACTION_DIGITS,
      message =
          "Target amount must be a valid monetary value with up to "
              + Constants.MAX_BALANCE_INTEGER_DIGITS
              + " integer digits and "
              + Constants.MAX_BALANCE_FRACTION_DIGITS
              + " decimal places")
  @Column(
      nullable = false,
      precision = Constants.MAX_BALANCE_INTEGER_DIGITS,
      scale = Constants.MAX_BALANCE_FRACTION_DIGITS)
  private BigDecimal target;

  @NotNull(message = "Contributed amount is required")
  @DecimalMin(
      value = Constants.MIN_BALANCE,
      message = "Contributed amount must be at least " + Constants.MIN_BALANCE)
  @Digits(
      integer = Constants.MAX_BALANCE_INTEGER_DIGITS,
      fraction = Constants.MAX_BALANCE_FRACTION_DIGITS,
      message =
          "Contributed amount must be a valid monetary value with up to "
              + Constants.MAX_BALANCE_INTEGER_DIGITS
              + " integer digits and "
              + Constants.MAX_BALANCE_FRACTION_DIGITS
              + " decimal places")
  @Column(
      nullable = false,
      precision = Constants.MAX_BALANCE_INTEGER_DIGITS,
      scale = Constants.MAX_BALANCE_FRACTION_DIGITS)
  private BigDecimal contributed = BigDecimal.ZERO;

  @NotNull(message = "Date is required")
  @Column(nullable = false)
  private LocalDate date;

  @Size(
      max = Constants.MAX_IMAGE_REF_LENGTH,
      message = "Image reference cannot exceed " + Constants.MAX_IMAGE_REF_LENGTH + " characters")
  @Column(nullable = true)
  private String imageRef;

  @ManyToOne
  @JoinColumn(name = "account_id", referencedColumnName = "id")
  private Account account;

  @ManyToOne
  @JoinColumn(name = "user_id", referencedColumnName = "id")
  private User user;

  public SavingGoal() {}

  public SavingGoal(
      String title,
      BigDecimal target,
      BigDecimal contributed,
      LocalDate date,
      String imageRef,
      Account account,
      User user) {
    this.title = title;
    this.target = target;
    this.contributed = contributed;
    this.date = date;
    this.imageRef = imageRef;
    this.account = account;
    this.user = user;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
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

  public Account getAccount() {
    return account;
  }

  public void setAccount(Account account) {
    this.account = account;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }
}
