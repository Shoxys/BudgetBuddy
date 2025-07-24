package com.shoxys.budgetbuddy_backend.DTOs.Transaction;

import java.time.LocalDate;

/**
 * Response object summarizing transaction data, including count and date range.
 */
public class TransactionSummaryResponse {
  private Integer count;
  private LocalDate earliest;
  private LocalDate latest;

  public TransactionSummaryResponse() {}

  public TransactionSummaryResponse(Integer count, LocalDate earliest, LocalDate latest) {
    this.count = count;
    this.earliest = earliest;
    this.latest = latest;
  }

  public Integer getCount() {
    return count;
  }

  public void setCount(Integer count) {
    this.count = count;
  }

  public LocalDate getEarliest() {
    return earliest;
  }

  public void setEarliest(LocalDate earliest) {
    this.earliest = earliest;
  }

  public LocalDate getLatest() {
    return latest;
  }

  public void setLatest(LocalDate latest) {
    this.latest = latest;
  }
}