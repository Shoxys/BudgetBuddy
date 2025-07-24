package com.shoxys.budgetbuddy_backend.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.shoxys.budgetbuddy_backend.Config.Constants;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.util.List;

/** Entity representing a user of the BudgetBuddy application. */
@Entity
@Table(name = "users")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank(message = "Email is required")
  @Email(message = "Please provide a valid email address")
  @Size(
      max = Constants.MAX_EMAIL_LENGTH,
      message = "Email cannot exceed " + Constants.MAX_EMAIL_LENGTH + " characters")
  @Column(unique = true, nullable = false)
  private String email;

  @NotBlank(message = "Hashed password is required")
  @Size(
      min = Constants.MIN_PASSWORD_LENGTH,
      max = Constants.MAX_PASSWORD_LENGTH,
      message =
          "Hashed password must be between "
              + Constants.MIN_PASSWORD_LENGTH
              + " and "
              + Constants.MAX_PASSWORD_LENGTH
              + " characters")
  @Column(nullable = false)
  private String hashedPassword;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonIgnore
  private List<Transaction> transactions;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonIgnore
  private List<Account> accounts;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonIgnore
  private List<SavingGoal> savingGoals;

  public User() {}

  public User(String email, String hashedPassword) {
    this.email = email;
    this.hashedPassword = hashedPassword;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getHashedPassword() {
    return hashedPassword;
  }

  public void setHashedPassword(String hashedPassword) {
    this.hashedPassword = hashedPassword;
  }

  public List<Transaction> getTransactions() {
    return transactions;
  }

  public void setTransactions(List<Transaction> transactions) {
    this.transactions = transactions;
  }

  public List<Account> getAccounts() {
    return accounts;
  }

  public void setAccounts(List<Account> accounts) {
    this.accounts = accounts;
  }

  public List<SavingGoal> getSavingGoals() {
    return savingGoals;
  }

  public void setSavingGoals(List<SavingGoal> savingGoals) {
    this.savingGoals = savingGoals;
  }
}
