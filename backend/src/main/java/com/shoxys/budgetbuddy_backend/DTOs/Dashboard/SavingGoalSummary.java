package com.shoxys.budgetbuddy_backend.DTOs.Dashboard;

import java.math.BigDecimal;

/** Summary of a saving goal's progress for dashboard display. */
public class SavingGoalSummary {
  private String title;
  private BigDecimal contributed;
  private BigDecimal target;

  public SavingGoalSummary(String title, BigDecimal contributed, BigDecimal target) {
    this.title = title;
    this.contributed = contributed;
    this.target = target;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public BigDecimal getContributed() {
    return contributed;
  }

  public void setContributed(BigDecimal contributed) {
    this.contributed = contributed;
  }

  public BigDecimal getTarget() {
    return target;
  }

  public void setTarget(BigDecimal target) {
    this.target = target;
  }
}
