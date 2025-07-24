package com.shoxys.budgetbuddy_backend.Enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Defines the status types for saving goals in the BudgetBuddy application.
 */
public enum GoalType {
  COMPLETED,
  IN_PROGRESS,
  OVERDUE,
  TOTAL;

  /**
   * Returns the JSON representation of the goal type.
   *
   * @return the enum name as a string
   */
  @JsonValue
  public String toJson() {
    return name();
  }
}