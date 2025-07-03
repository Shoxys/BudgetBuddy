package com.shoxys.budgetbuddy_backend.DTOs;

import com.shoxys.budgetbuddy_backend.Enums.SourceType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

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

    private String merchant;

    @NotNull(message = "Balance is required")
    @DecimalMin(value = "0.01", message = "Balance must be greater than 0")
    @Digits(integer = 12, fraction = 2, message = "Balance must be a valid monetary value")
    private BigDecimal balanceAtTransaction;

    @NotNull(message = "Source is required")
    private SourceType source;

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

    public String getMerchant() {
        return merchant;
    }

    public void setMerchant(String merchant) {
        this.merchant = merchant;
    }

    public BigDecimal getBalanceAtTransaction() {
        return balanceAtTransaction;
    }

    //TODO: Add logic for this

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

