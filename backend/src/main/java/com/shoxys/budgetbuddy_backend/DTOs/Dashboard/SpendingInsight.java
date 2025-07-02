package com.shoxys.budgetbuddy_backend.DTOs.Dashboard;

public class SpendingInsight {
    private String insight;

    public SpendingInsight(String insight) {
        this.insight = insight;
    }

    public String getInsight() {
        return insight;
    }

    public void setInsight(String insight) {
        this.insight = insight;
    }
}
