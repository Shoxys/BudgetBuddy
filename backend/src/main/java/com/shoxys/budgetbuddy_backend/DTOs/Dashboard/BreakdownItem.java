package com.shoxys.budgetbuddy_backend.DTOs.Dashboard;

import java.math.BigDecimal;

public class BreakdownItem {
  private String name;
  private BigDecimal value;

  public BreakdownItem(String name, BigDecimal value) {
    this.name = name;
    this.value = value;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public BigDecimal getValue() {
    return value;
  }

  public void setValue(BigDecimal value) {
    this.value = value;
  }
}
