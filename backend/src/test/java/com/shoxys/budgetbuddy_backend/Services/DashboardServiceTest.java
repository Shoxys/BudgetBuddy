package com.shoxys.budgetbuddy_backend.Services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.shoxys.budgetbuddy_backend.DTOs.Dashboard.*;
import com.shoxys.budgetbuddy_backend.Entities.Account;
import com.shoxys.budgetbuddy_backend.Entities.SavingGoal;
import com.shoxys.budgetbuddy_backend.Repo.AccountRepo;
import com.shoxys.budgetbuddy_backend.Repo.SavingGoalsRepo;
import com.shoxys.budgetbuddy_backend.Repo.TransactionRepo;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {
  @Mock private AccountRepo accountRepo;
  @Mock private SavingGoalsRepo savingGoalsRepo;
  @Mock private TransactionRepo transactionRepo;

  @InjectMocks private DashboardService dashboardService;

  private final long userId = 1L;

  private final Account mockAccount = new Account();
  private final SavingGoal mockGoal = new SavingGoal();
  private final BigDecimal totalBalance = BigDecimal.valueOf(1000);

  @BeforeEach
  void setup() {
    mockAccount.setName("Main");
    mockAccount.setBalance(BigDecimal.valueOf(1000));
    mockGoal.setTitle("Save for Car");
    mockGoal.setContributed(BigDecimal.valueOf(500));
    mockGoal.setTarget(BigDecimal.valueOf(1000));
  }

  @Test
  void getTotalBalance_shouldReturnCorrectBalance() {
    when(accountRepo.findTotalBalanceByUserId(userId)).thenReturn(totalBalance);
    assertEquals(totalBalance, dashboardService.getTotalBalance(userId));
  }

  @Test
  void getAccountSummary_shouldMapCorrectly() {
    mockAccount.setType(null);
    when(accountRepo.findAccountsTypeNameBalanceByUser_Id(userId))
        .thenReturn(Collections.singletonList(mockAccount));
    List<AccountSummary> summaries = dashboardService.getAccountSummary(userId);

    assertEquals(1, summaries.size());
    assertEquals(mockAccount.getName(), summaries.get(0).getName());
    assertEquals(mockAccount.getBalance(), summaries.get(0).getBalance());
  }

  @Test
  void getNetworthResponse_shouldMapCorrectly() {
    when(accountRepo.findAccountsNameBalanceByUser_Id(userId))
        .thenReturn(Collections.singletonList(mockAccount));
    when(accountRepo.findTotalBalanceByUserId(userId)).thenReturn(totalBalance);

    NetworthResponse response = dashboardService.getNetworthResponse(userId);
    assertEquals(totalBalance, response.getTotal());
    assertEquals(mockAccount.getName(), response.getBreakdownItems().get(0).getName());
    assertEquals(mockAccount.getBalance(), response.getBreakdownItems().get(0).getValue());
  }

  @Test
  void getSavingGoalSummary_shouldMapCorrectly() {
    when(savingGoalsRepo.findTop3ByUser_IdOrderByTargetDesc(userId))
        .thenReturn(Collections.singletonList(mockGoal));
    List<SavingGoalSummary> summaries = dashboardService.getSavingGoalSummary(userId);

    assertEquals(1, summaries.size());
    assertEquals(mockGoal.getTitle(), summaries.get(0).getTitle());
    assertEquals(mockGoal.getContributed(), summaries.get(0).getContributed());
    assertEquals(mockGoal.getTarget(), summaries.get(0).getTarget());
  }

  @Test
  void getIncomeExpenseSummary_shouldReturnCorrectData() {
    BigDecimal incomeThisMonth = BigDecimal.valueOf(3000);
    BigDecimal expenseThisMonth = BigDecimal.valueOf(1200);
    BigDecimal incomeLastMonth = BigDecimal.valueOf(2500);
    BigDecimal expenseLastMonth = BigDecimal.valueOf(1000);

    LocalDate lastMonthStart = LocalDate.now().minusMonths(1).withDayOfMonth(1);
    LocalDate lastMonthEnd = lastMonthStart.withDayOfMonth(lastMonthStart.lengthOfMonth());

    when(transactionRepo.getTotalCreditThisMonth(userId)).thenReturn(incomeThisMonth);
    when(transactionRepo.getTotalDebitThisMonth(userId)).thenReturn(expenseThisMonth);
    when(transactionRepo.getTotalCreditBetween(userId, lastMonthStart, lastMonthEnd))
        .thenReturn(incomeLastMonth);
    when(transactionRepo.getTotalDebitBetween(userId, lastMonthStart, lastMonthEnd))
        .thenReturn(expenseLastMonth);

    List<IncomeExpenseSummary> result = dashboardService.getIncomeExpenseSummary(userId);

    assertEquals(2, result.size());
    assertEquals(incomeThisMonth, result.get(0).getIncome());
    assertEquals(expenseLastMonth, result.get(1).getExpenses());
  }

  @Test
  void getExpenseAnalysis_shouldReturnExpectedList() {
    ExpenseAnalysis mockAnalysis = new ExpenseAnalysis();
    mockAnalysis.setLabel("Food");
    mockAnalysis.setValue(BigDecimal.valueOf(500));

    when(transactionRepo.findTop5CategoriesByAmount(userId))
        .thenReturn(Collections.singletonList(mockAnalysis));

    List<ExpenseAnalysis> result = dashboardService.getExpenseAnalysis(userId);
    assertEquals(1, result.size());
    assertEquals("Food", result.get(0).getLabel());
    assertEquals(mockAnalysis.getValue(), result.get(0).getValue());
  }

  @Test
  void getRecentTransactions_shouldReturnExpectedList() {
    RecentTransactions txn = new RecentTransactions();
    txn.setDescription("Lunch");

    when(transactionRepo.findLatest3TransactionSummaries(userId))
        .thenReturn(Collections.singletonList(txn));
    List<RecentTransactions> result = dashboardService.getRecentTransactions(userId);

    assertEquals(1, result.size());
    assertEquals("Lunch", result.get(0).getDescription());
  }

  @Test
  void getIncomeTrend_shouldReturnIncomeTrendForCurrentAndLastYear() {
    int currentYear = Year.now().getValue();
    int lastYear = currentYear - 1;

    List<BigDecimal> thisYearIncome = Collections.nCopies(12, BigDecimal.TEN);
    List<BigDecimal> lastYearIncome = Collections.nCopies(12, BigDecimal.ONE);
    List<String> months =
        Arrays.asList(
            "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec");

    when(transactionRepo.getMonthlyIncomeForYear(userId, currentYear)).thenReturn(thisYearIncome);
    when(transactionRepo.getMonthlyIncomeForYear(userId, lastYear)).thenReturn(lastYearIncome);

    IncomeTrend trend = dashboardService.getIncomeTrend(userId);

    assertEquals(months, trend.getMonths());
    assertEquals(thisYearIncome, trend.getIncomeThisYear());
    assertEquals(lastYearIncome, trend.getIncomeLastYear());
  }

  // TODO: Implement real tests where added
  @Test
  void getSpendingInsights_shouldReturnPredefinedInsights() {
    List<SpendingInsight> insights = dashboardService.getSpendingInsights(userId);

    assertEquals(2, insights.size());
    assertEquals(
        "Consider shifting just 1 meal/week to home cooking, you could save $120/month while still enjoying the occasional treat!",
        insights.get(0).getInsight());
    assertEquals(
        "Bundling or pausing just 1–2 rarely used ones could save around $25–$40/month, with no major impact on your routine!",
        insights.get(1).getInsight());
  }
}
