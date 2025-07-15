package com.shoxys.budgetbuddy_backend.DTOs;

import java.math.BigDecimal;

public class GoalContributionRequest {
    private BigDecimal contribution;

    public GoalContributionRequest() {
    }

    public GoalContributionRequest(BigDecimal contribution) {
        this.contribution = contribution;
    }

    public BigDecimal getContribution() {
        return contribution;
    }

    public void setContribution(BigDecimal contribution) {
        this.contribution = contribution;
    }
}
