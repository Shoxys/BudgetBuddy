package com.shoxys.budgetbuddy_backend.DTOs.Dashboard;

import java.math.BigDecimal;

public class IncomeExpenseSummary {
    private String name;
    private BigDecimal income;
    private BigDecimal expenses;

    public IncomeExpenseSummary(String name, BigDecimal income, BigDecimal expenses) {
        this.name = name;
        this.income = income;
        this.expenses = expenses;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getIncome() {
        return income;
    }

    public void setIncome(BigDecimal income) {
        this.income = income;
    }

    public BigDecimal getExpenses() {
        return expenses;
    }

    public void setExpenses(BigDecimal expenses) {
        this.expenses = expenses;
    }
}
