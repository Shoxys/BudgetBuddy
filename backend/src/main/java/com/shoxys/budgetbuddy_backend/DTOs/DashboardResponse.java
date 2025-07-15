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

  public DashboardResponse(
      BigDecimal totalBalance,
      List<AccountSummary> accountSummaries,
      NetworthResponse networthResponse,
      List<SpendingInsight> spendingInsights,
      List<SavingGoalSummary> savingGoalSummary,
      List<IncomeExpenseSummary> incomeExpenseSummary,
      IncomeTrend incomeTrend,
      List<ExpenseAnalysis> expenseAnalysis,
      List<RecentTransactions> recentTransactions) {
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

  public BigDecimal getTotalBalance() {
    return totalBalance;
  }

  public void setTotalBalance(BigDecimal totalBalance) {
    this.totalBalance = totalBalance;
  }

  public List<AccountSummary> getAccountSummaries() {
    return accountSummaries;
  }

  public void setAccountSummaries(List<AccountSummary> accountSummaries) {
    this.accountSummaries = accountSummaries;
  }

  public NetworthResponse getNetworthResponse() {
    return networthResponse;
  }

  public void setNetworthResponse(NetworthResponse networthResponse) {
    this.networthResponse = networthResponse;
  }

  public List<SpendingInsight> getSpendingInsights() {
    return spendingInsights;
  }

  public void setSpendingInsights(List<SpendingInsight> spendingInsights) {
    this.spendingInsights = spendingInsights;
  }

  public List<SavingGoalSummary> getSavingGoalSummary() {
    return savingGoalSummary;
  }

  public void setSavingGoalSummary(List<SavingGoalSummary> savingGoalSummary) {
    this.savingGoalSummary = savingGoalSummary;
  }

  public List<IncomeExpenseSummary> getIncomeExpenseSummary() {
    return incomeExpenseSummary;
  }

  public void setIncomeExpenseSummary(List<IncomeExpenseSummary> incomeExpenseSummary) {
    this.incomeExpenseSummary = incomeExpenseSummary;
  }

  public IncomeTrend getIncomeTrend() {
    return incomeTrend;
  }

  public void setIncomeTrend(IncomeTrend incomeTrend) {
    this.incomeTrend = incomeTrend;
  }

  public List<ExpenseAnalysis> getExpenseAnalysis() {
    return expenseAnalysis;
  }

  public void setExpenseAnalysis(List<ExpenseAnalysis> expenseAnalysis) {
    this.expenseAnalysis = expenseAnalysis;
  }

  public List<RecentTransactions> getRecentTransactions() {
    return recentTransactions;
  }

  public void setRecentTransactions(List<RecentTransactions> recentTransactions) {
    this.recentTransactions = recentTransactions;
  }
}
