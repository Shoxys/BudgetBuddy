package com.shoxys.budgetbuddy_backend.DTOs;

import com.shoxys.budgetbuddy_backend.DTOs.Dashboard.*;

import java.math.BigDecimal;
import java.util.List;

public class DashboardResponse {

    private BigDecimal totalBalance;
    private List<AccountSummary> accountSummaries;
    private NetworthResponse networthResponse;
    private List<SpendingInsight> spendingInsights;
    private List<SavingGoalSummary> savingGoalSummary;
    private List<IncomeExpenseSummary> incomeExpenseSummary;
    private IncomeTrend incomeTrend;
    private List<ExpenseAnalysis> expenseAnalysis;
    private List<RecentTransactions> recentTransactions;

    public DashboardResponse(BigDecimal totalBalance, List<AccountSummary> accountSummaries, NetworthResponse networthResponse, List<SpendingInsight> spendingInsights, List<SavingGoalSummary> savingGoalSummary, List<IncomeExpenseSummary> incomeExpenseSummary, IncomeTrend incomeTrend, List<ExpenseAnalysis> expenseAnalysis, List<RecentTransactions> recentTransactions) {
        this.totalBalance = totalBalance;
        this.accountSummaries = accountSummaries;
        this.networthResponse = networthResponse;
        this.spendingInsights = spendingInsights;
        this.savingGoalSummary = savingGoalSummary;
        this.incomeExpenseSummary = incomeExpenseSummary;
        this.incomeTrend = incomeTrend;
        this.expenseAnalysis = expenseAnalysis;
        this.recentTransactions = recentTransactions;
    }
}
