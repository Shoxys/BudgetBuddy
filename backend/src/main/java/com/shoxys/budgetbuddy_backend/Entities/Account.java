package com.shoxys.budgetbuddy_backend.Entities;

import com.shoxys.budgetbuddy_backend.Enums.AccountType;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "accounts")
public class Account {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column(nullable = false)
  private String name;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private AccountType type;

  @Column(nullable = true)
  private Integer accountNo;

  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal balance;

  @Column(nullable = false)
  private boolean isManual;

  @ManyToOne
  @JoinColumn(name = "user_id", referencedColumnName = "id")
  private User user;

  public Account() {}

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

  public long getId() {
    return id;
  }

  public void setId(long id) {
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

  public boolean getIsManual() {
    return isManual;
  }

  public void setManual(boolean manual) {
    isManual = manual;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }
}
