package com.shoxys.budgetbuddy_backend.DTOs.SavingGoal;

import java.math.BigDecimal;

/**
 * Request object for adding a contribution to a saving goal.
 */
public class GoalContributionRequest {
  private BigDecimal contribution;

  public GoalContributionRequest() {}

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