package com.shoxys.budgetbuddy_backend.DTOs.Dashboard;

import java.math.BigDecimal;
import java.util.List;

public class IncomeTrend {
    private List<String> months;
    private List<BigDecimal> income;
    private List<BigDecimal> expenses;

    public IncomeTrend(List<String> months, List<BigDecimal> income, List<BigDecimal> expenses) {
        this.months = months;
        this.income = income;
        this.expenses = expenses;
    }

    public List<String> getMonths() {
        return months;
    }

    public void setMonths(List<String> months) {
        this.months = months;
    }

    public List<BigDecimal> getIncome() {
        return income;
    }

    public void setIncome(List<BigDecimal> income) {
        this.income = income;
    }

    public List<BigDecimal> getExpenses() {
        return expenses;
    }

    public void setExpenses(List<BigDecimal> expenses) {
        this.expenses = expenses;
    }
}
