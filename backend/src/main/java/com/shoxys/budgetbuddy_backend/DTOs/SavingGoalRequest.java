package com.shoxys.budgetbuddy_backend.DTOs;

import com.shoxys.budgetbuddy_backend.Entities.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public class SavingGoalRequest {

    @NotBlank(message = "title is required")
    private String title;

    @NotNull(message = "Target amount is required")
    @DecimalMin(value = "0.01", message = "Target amount must be greater than 0")
    @Digits(integer = 12, fraction = 2, message = "Target amount must be a valid monetary value")
    private BigDecimal target;

    @NotNull(message = "Contribution amount is required")
    @DecimalMin(value = "0.01", message = "Contribution amount  must be greater than 0")
    @Digits(integer = 12, fraction = 2, message = "Contribution amount  must be a valid monetary value")
    private BigDecimal contributed;

    @NotNull(message = "Date is required")
    private LocalDate date;

    private String imageRef;

    public SavingGoalRequest() {
    }

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

