package com.shoxys.budgetbuddy_backend.DTOs;

import java.util.List;

public class GoalStatsResponse {
    private List<GoalStat> goalStats;

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
