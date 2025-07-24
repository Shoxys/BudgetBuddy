package com.shoxys.budgetbuddy_backend.DTOs.Dashboard;

import java.math.BigDecimal;
import java.util.List;

/**
 * Represents income trends over months for dashboard display, comparing current and previous year.
 */

public class IncomeTrend {
  private List<String> months;
  private List<BigDecimal> incomeThisYear;
  private List<BigDecimal> incomeLastYear;

  public IncomeTrend(
      List<String> months, List<BigDecimal> incomeThisYear, List<BigDecimal> incomeLastYear) {
    this.months = months;
    this.incomeThisYear = incomeThisYear;
    this.incomeLastYear = incomeLastYear;
  }

  public List<String> getMonths() {
    return months;
  }

  public void setMonths(List<String> months) {
    this.months = months;
  }

  public List<BigDecimal> getIncomeThisYear() {
    return incomeThisYear;
  }

  public void setIncomeThisYear(List<BigDecimal> incomeThisYear) {
    this.incomeThisYear = incomeThisYear;
  }

  public List<BigDecimal> getIncomeLastYear() {
    return incomeLastYear;
  }

  public void setIncomeLastYear(List<BigDecimal> incomeLastYear) {
    this.incomeLastYear = incomeLastYear;
  }
}
