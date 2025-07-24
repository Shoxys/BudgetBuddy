package com.shoxys.budgetbuddy_backend.DTOs.SavingGoal;

import java.util.List;

/**
 * Response object containing a list of saving goal statistics.
 */
public class GoalStatsResponse {
  private List<GoalStat> goalStats;

  public GoalStatsResponse() {}

  public GoalStatsResponse(List<GoalStat> goalStats) {
    this.goalStats = goalStats;
  }

  public List<GoalStat> getGoalStats() {
    return goalStats;
  }

  public void setGoalStats(List<GoalStat> goalStats) {
    this.goalStats = goalStats;
  }
}