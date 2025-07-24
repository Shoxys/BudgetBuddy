package com.shoxys.budgetbuddy_backend.DTOs.SavingGoal;

import com.shoxys.budgetbuddy_backend.Enums.GoalType;

/**
 * Represents a statistic or insight for a saving goal, including its type and amount.
 */
public class GoalStat {
  private String insight;
  private GoalType goalType;
  private int amount; // Represents a count or metric, not a monetary value

  public GoalStat(String insight, GoalType goalType, int amount) {
    this.insight = insight;
    this.goalType = goalType;
    this.amount = amount;
  }

  public String getInsight() {
    return insight;
  }

  public void setInsight(String insight) {
    this.insight = insight;
  }

  public GoalType getGoalType() {
    return goalType;
  }

  public void setGoalType(GoalType goalType) {
    this.goalType = goalType;
  }

  public int getAmount() {
    return amount;
  }

  public void setAmount(int amount) {
    this.amount = amount;
  }
}