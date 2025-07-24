package com.shoxys.budgetbuddy_backend.Services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.shoxys.budgetbuddy_backend.DTOs.Dashboard.*;
import com.shoxys.budgetbuddy_backend.Entities.Account;
import com.shoxys.budgetbuddy_backend.Entities.SavingGoal;
import com.shoxys.budgetbuddy_backend.Entities.Transaction;
import com.shoxys.budgetbuddy_backend.Repo.AccountRepo;
import com.shoxys.budgetbuddy_backend.Repo.SavingGoalsRepo;
import com.shoxys.budgetbuddy_backend.Repo.TransactionRepo;
import com.shoxys.budgetbuddy_backend.Repo.UserRepo;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Year;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;
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
  @Mock private UserRepo userRepo;
  @Mock private TransactionService transactionService;
  @Mock private AiInsightService aiInsightService;

  @InjectMocks private DashboardService dashboardService;

  private final long userId = 1L;

  private final Account mockAccount = new Account();
  private final SavingGoal mockGoal = new SavingGoal();
  private final BigDecimal totalBalance = BigDecimal.valueOf(1000);

  @BeforeEach
  void setup() {
    mockAccount.setId(1L);
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
    when(accountRepo.findAccountsTypeNameBalanceByUserId(userId))
        .thenReturn(Collections.singletonList(mockAccount));
    List<AccountSummary> summaries = dashboardService.getAccountSummary(userId);

    assertEquals(1, summaries.size());
    assertEquals(mockAccount.getName(), summaries.getFirst().getName());
    assertEquals(mockAccount.getBalance(), summaries.getFirst().getBalance());
  }

  @Test
  void getNetworthResponse_shouldMapCorrectly() {
    when(dashboardService.getTotalBalance(userId)).thenReturn(mockAccount.getBalance());

    when(accountRepo.findAccountsNameBalanceByUserId(userId))
        .thenReturn(Collections.singletonList(mockAccount));

    NetworthResponse response = dashboardService.getNetworthResponse(userId);

    assertEquals(
        mockAccount.getBalance(), response.getTotal(), "Total should match the account balance");
    assertEquals(
        mockAccount.getName(),
        response.getBreakdownItems().getFirst().getName(),
        "Breakdown item name should match");
    assertEquals(
        mockAccount.getBalance(),
        response.getBreakdownItems().getFirst().getValue(),
        "Breakdown item value should match");
  }

  @Test
  void getSavingGoalSummary_shouldMapCorrectly() {
    when(savingGoalsRepo.findTop3ByUser_IdOrderByTargetDesc(userId))
        .thenReturn(Collections.singletonList(mockGoal));
    List<SavingGoalSummary> summaries = dashboardService.getSavingGoalSummary(userId);

    assertEquals(1, summaries.size());
    assertEquals(mockGoal.getTitle(), summaries.getFirst().getTitle());
    assertEquals(mockGoal.getContributed(), summaries.getFirst().getContributed());
    assertEquals(mockGoal.getTarget(), summaries.getFirst().getTarget());
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

    when(transactionRepo.findTop5ExpenseCategoriesByAmount(userId))
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
    // Arrange
    int currentYear = Year.now().getValue();
    int lastYear = currentYear - 1;

    List<String> months =
        Arrays.asList(
            "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec");
    List<Object[]> thisYearIncome =
        IntStream.rangeClosed(1, 12)
            .mapToObj(
                i ->
                    new Object[] {
                      i,
                      BigDecimal.valueOf(100.00 + (i - 1) * 100.00)
                          .setScale(2, RoundingMode.HALF_UP)
                    })
            .toList();
    List<Object[]> lastYearIncome =
        IntStream.rangeClosed(1, 12)
            .mapToObj(
                i ->
                    new Object[] {
                      i,
                      BigDecimal.valueOf(50.00 + (i - 1) * 100.00).setScale(2, RoundingMode.HALF_UP)
                    })
            .toList();

    // Expected BigDecimal lists for assertions
    List<BigDecimal> expectedThisYearIncome =
        thisYearIncome.stream().map(row -> (BigDecimal) row[1]).toList();
    List<BigDecimal> expectedLastYearIncome =
        lastYearIncome.stream().map(row -> (BigDecimal) row[1]).toList();

    when(transactionRepo.getMonthlyIncomeForYear(userId, currentYear)).thenReturn(thisYearIncome);
    when(transactionRepo.getMonthlyIncomeForYear(userId, lastYear)).thenReturn(lastYearIncome);

    // Act
    IncomeTrend trend = dashboardService.getIncomeTrend(userId);

    // Assert
    assertEquals(months, trend.getMonths(), "Months should match");
    assertEquals(
        expectedThisYearIncome, trend.getIncomeThisYear(), "Current year income should match");
    assertEquals(
        expectedLastYearIncome, trend.getIncomeLastYear(), "Last year income should match");
  }

  @Test
  void getSpendingInsights_shouldReturnInsights() {
    // Arrange
    LocalDate endDate = LocalDate.now();
    LocalDate startDate = endDate.minusDays(30);

    Transaction t1 = new Transaction();
    t1.setAmount(BigDecimal.valueOf(-50.00));
    t1.setCategory("Food");
    Transaction t2 = new Transaction();
    t2.setAmount(BigDecimal.valueOf(-25.00));
    t2.setCategory("Subscription");
    List<Transaction> mockTransactions = Arrays.asList(t1, t2);

    String transactionStats =
        "User's spending over the last 30 days:\n"
            + "- Food: $50.00\n"
            + "- Subscription: $25.00\n";
    String rawInsights =
        "Insight 1: Consider shifting just 1 meal/week to home cooking, you could save $120/month while still enjoying the occasional treat!\n"
            + "Insight 2: Bundling or pausing just 1–2 rarely used ones could save around $25–$40/month, with no major impact on your routine!";
    List<SpendingInsight> expectedInsights =
        Arrays.asList(
            new SpendingInsight(
                "Consider shifting just 1 meal/week to home cooking, you could save $120/month while still enjoying the occasional treat!"),
            new SpendingInsight(
                "Bundling or pausing just 1–2 rarely used ones could save around $25–$40/month, with no major impact on your routine!"));

    when(transactionService.getTransactionsByUserIdInTimeFrame(userId, startDate, endDate))
        .thenReturn(mockTransactions);
    when(aiInsightService.buildStrictPrompt(transactionStats)).thenReturn("mocked prompt");
    when(aiInsightService.getInsightsFromText("mocked prompt")).thenReturn(rawInsights);
    when(aiInsightService.parseInsightsToList(rawInsights)).thenReturn(expectedInsights);

    // Act
    List<SpendingInsight> insights = dashboardService.getSpendingInsights(userId);

    // Assert
    assertEquals(2, insights.size(), "Should return 2 insights");
    assertEquals(
        expectedInsights.get(0).getInsight(),
        insights.get(0).getInsight(),
        "First insight should match");
    assertEquals(
        expectedInsights.get(1).getInsight(),
        insights.get(1).getInsight(),
        "Second insight should match");
  }
}
