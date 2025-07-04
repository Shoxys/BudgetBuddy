package com.shoxys.budgetbuddy_backend.Services;

import com.shoxys.budgetbuddy_backend.DTOs.Dashboard.*;
import com.shoxys.budgetbuddy_backend.DTOs.DashboardResponse;
import com.shoxys.budgetbuddy_backend.Entities.Account;
import com.shoxys.budgetbuddy_backend.Entities.SavingGoal;
import com.shoxys.budgetbuddy_backend.Repo.AccountRepo;
import com.shoxys.budgetbuddy_backend.Repo.SavingGoalsRepo;
import com.shoxys.budgetbuddy_backend.Repo.TransactionRepo;
import com.shoxys.budgetbuddy_backend.Security.AppUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Year;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class DashboardService {
    @Autowired
    private AccountRepo accountRepo;
    @Autowired
    private SavingGoalsRepo savingGoalsRepo;
    @Autowired
    private TransactionRepo transactionRepo;

    public DashboardResponse getDashboardResponse(long userId) {
        // All required dashboard data elements
        BigDecimal totalBalance = getTotalBalance(userId);
        List<AccountSummary> accountSummaryList = getAccountSummary(userId);
        NetworthResponse networthResponse = getNetworthResponse(userId);
        List<SpendingInsight> spendingInsights = getSpendingInsights(userId);
        List<SavingGoalSummary>  savingGoalSummary = getSavingGoalSummary(userId);
        List<IncomeExpenseSummary> incomeExpenseSummary = getIncomeExpenseSummary(userId);
        IncomeTrend incomeTrend =  getIncomeTrend(userId);
        List<ExpenseAnalysis> expenseAnalysis = getExpenseAnalysis(userId);
        List<RecentTransactions> recentTransactions = getRecentTransactions(userId);

        return new DashboardResponse(totalBalance, accountSummaryList, networthResponse, spendingInsights, savingGoalSummary,
                incomeExpenseSummary, incomeTrend, expenseAnalysis, recentTransactions);
    }

    public BigDecimal getTotalBalance(long userId) {
        return accountRepo.findTotalBalanceByUserId(userId);
    }

    public List<AccountSummary> getAccountSummary(long userId) {
        List<Account> accounts = accountRepo.findAccountsTypeNameBalanceByUser_Id(userId);

        return accounts.stream()
                .map(account -> new AccountSummary(account.getType(), account.getName(), account.getBalance()))
                .toList();
    }

    public NetworthResponse getNetworthResponse(long userId) {
        List<Account> accounts = accountRepo.findAccountsNameBalanceByUser_Id(userId);

        BigDecimal totalBalance = getTotalBalance(userId);
        List<BreakdownItem> breakdownItemList = accounts.stream()
                .map(account -> new BreakdownItem(account.getName(), account.getBalance()))
                .toList();

        return new NetworthResponse(totalBalance, breakdownItemList);
    }

    public List<SpendingInsight> getSpendingInsights(long userId) {
        //TODO: Implement AI insights API?
        SpendingInsight insight1 = new SpendingInsight("Consider shifting just 1 meal/week to home cooking, you could save $120/month while still enjoying the occasional treat!");
        SpendingInsight insight2 = new SpendingInsight("Bundling or pausing just 1–2 rarely used ones could save around $25–$40/month, with no major impact on your routine!");

        List<SpendingInsight> spendingInsights = new ArrayList<>();
        spendingInsights.add(insight1);
        spendingInsights.add(insight2);

        return spendingInsights;
    }

    public List<SavingGoalSummary> getSavingGoalSummary(long userId) {
        List<SavingGoal> goalSummary = savingGoalsRepo.findTop3ByUser_IdOrderByTargetDesc(userId);

        return goalSummary.stream()
                .map(goal -> new SavingGoalSummary(goal.getTitle(), goal.getContributed(), goal.getTarget()))
                .toList();
    }

    public List<IncomeExpenseSummary> getIncomeExpenseSummary(long userId) {
        BigDecimal incomeThisMonth = transactionRepo.getTotalCreditThisMonth(userId);
        BigDecimal expensesThisMonth = transactionRepo.getTotalDebitThisMonth(userId);

        BigDecimal incomeLastMonth = transactionRepo.getTotalCreditLastMonth(userId);
        BigDecimal expensesLastMonth = transactionRepo.getTotalDebitLastMonth(userId);

        IncomeExpenseSummary incomeExpenseThisMonth = new IncomeExpenseSummary("This month", incomeThisMonth, expensesThisMonth);
        IncomeExpenseSummary incomeExpenseLastMonth = new IncomeExpenseSummary("Last month", incomeLastMonth, expensesLastMonth);

        return Arrays.asList(incomeExpenseThisMonth, incomeExpenseLastMonth);
    }

    public IncomeTrend getIncomeTrend(long userId) {
        List<String> months = Arrays.asList("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec");
        int currentYear = Year.now().getValue();
        int lastYear =  currentYear - 1;

        List<BigDecimal> incomeThisYear = transactionRepo.getMonthlyIncomeForYear(userId, currentYear);
        List<BigDecimal> incomeLastYear = transactionRepo.getMonthlyIncomeForYear(userId, lastYear);

        return new IncomeTrend(months, incomeThisYear, incomeLastYear);
    }

    public List<ExpenseAnalysis> getExpenseAnalysis(long userId) {
        return transactionRepo.findTop5CategoriesByAmountNative(userId);
    }

    public List<RecentTransactions> getRecentTransactions(long userId) {
        return transactionRepo.findLatest3TransactionSummaries(userId);
    }
}
