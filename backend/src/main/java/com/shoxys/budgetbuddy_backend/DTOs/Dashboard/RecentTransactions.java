package com.shoxys.budgetbuddy_backend.DTOs.Dashboard;

import java.math.BigDecimal;
import java.time.LocalDate;

public class RecentTransactions {
    private LocalDate date;
    private String description;
    private String category;
    private BigDecimal amount;

    public RecentTransactions(LocalDate date, String description, String category, BigDecimal amount) {
        this.date = date;
        this.description = description;
        this.category = category;
        this.amount = amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
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

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
