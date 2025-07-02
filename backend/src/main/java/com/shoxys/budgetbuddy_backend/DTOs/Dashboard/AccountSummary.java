package com.shoxys.budgetbuddy_backend.DTOs.Dashboard;

import com.shoxys.budgetbuddy_backend.Enums.AccountType;

import java.math.BigDecimal;

public class AccountSummary {
    private AccountType type;
    private String name;
    private BigDecimal balance;

    public AccountSummary(AccountType type, String name, BigDecimal balance) {
        this.type = type;
        this.name = name;
        this.balance = balance;
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
