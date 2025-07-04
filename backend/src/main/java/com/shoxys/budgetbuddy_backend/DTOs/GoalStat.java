package com.shoxys.budgetbuddy_backend.DTOs;

import com.shoxys.budgetbuddy_backend.Enums.GoalType;

public class GoalStat {
    private String insight;
    private GoalType goalType;
    private int amount;

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
