package com.shoxys.budgetbuddy_backend.DTOs.Dashboard;

import java.math.BigDecimal;

/** Represents an expense analysis item for dashboard display. */
public class ExpenseAnalysis {
  private String label;
  private BigDecimal value;

  public ExpenseAnalysis() {}

  public ExpenseAnalysis(String label, BigDecimal value) {
    this.label = label;
    this.value = value;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public BigDecimal getValue() {
    return value;
  }

  public void setValue(BigDecimal value) {
    this.value = value;
  }
}
