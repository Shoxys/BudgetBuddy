package com.shoxys.budgetbuddy_backend.Services;

import com.shoxys.budgetbuddy_backend.DTOs.Dashboard.*;
import com.shoxys.budgetbuddy_backend.Entities.Account;
import com.shoxys.budgetbuddy_backend.Entities.SavingGoal;
import com.shoxys.budgetbuddy_backend.Entities.Transaction;
import com.shoxys.budgetbuddy_backend.Repo.AccountRepo;
import com.shoxys.budgetbuddy_backend.Repo.SavingGoalsRepo;
import com.shoxys.budgetbuddy_backend.Repo.TransactionRepo;
import com.shoxys.budgetbuddy_backend.Utils.Utils;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;
import java.util.*;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service for generating dashboard-related data, including account summaries, net worth, spending
 * insights, saving goals, income/expense summaries, income trends, expense analysis, and recent
 * transactions.
 */
@Service
public class DashboardService {
  private static final Logger logger = LoggerFactory.getLogger(DashboardService.class);
  private static final int SPENDING_INSIGHT_DAYS = 30;
  private static final int TOP_SAVING_GOALS_LIMIT = 3;
  private static final int TOP_EXPENSE_CATEGORIES_LIMIT = 5;
  private static final int RECENT_TRANSACTIONS_LIMIT = 3;

  private final AccountRepo accountRepo;
  private final SavingGoalsRepo savingGoalsRepo;
  private final TransactionRepo transactionRepo;
  private final TransactionService transactionService;
  private final AiInsightService aiInsightService;

  /**
   * Constructs a DashboardService with required dependencies.
   *
   * @param accountRepo Repository for account-related data access
   * @param savingGoalsRepo Repository for saving goals data access
   * @param transactionRepo Repository for transaction data access
   * @param transactionService Service for transaction-related operations
   * @param aiInsightService Service for generating AI-based spending insights
   */
  @Autowired
  public DashboardService(
      AccountRepo accountRepo,
      SavingGoalsRepo savingGoalsRepo,
      TransactionRepo transactionRepo,
      TransactionService transactionService,
      AiInsightService aiInsightService) {
    this.accountRepo = accountRepo;
    this.savingGoalsRepo = savingGoalsRepo;
    this.transactionRepo = transactionRepo;
    this.transactionService = transactionService;
    this.aiInsightService = aiInsightService;
  }

  /**
   * Retrieves the total balance across all accounts for a given user.
   *
   * @param userId the ID of the user
   * @return the total balance, or zero if no accounts exist
   * @throws IllegalArgumentException if userId is not positive
   */
  public BigDecimal getTotalBalance(long userId) {
    logger.debug("Fetching total balance for userId: {}", userId);
    Utils.validatePositiveId(userId, "User ID must be positive");
    BigDecimal total = accountRepo.findTotalBalanceByUserId(userId);
    BigDecimal result = total != null ? total : BigDecimal.ZERO;
    logger.info("Total balance for userId {}: {}", userId, result);
    return result;
  }

  /**
   * Retrieves a summary of all accounts for a user, including ID, type, name, and balance.
   *
   * @param userId the ID of the user
   * @return a list of account summaries
   * @throws IllegalArgumentException if userId is not positive
   */
  public List<AccountSummary> getAccountSummary(long userId) {
    logger.debug("Fetching account summary for userId: {}", userId);
    Utils.validatePositiveId(userId, "User ID must be positive");
    List<Account> accounts = accountRepo.findAccountsTypeNameBalanceByUserId(userId);
    List<AccountSummary> summaries =
        accounts.stream()
            .map(
                account ->
                    new AccountSummary(
                        account.getId(),
                        account.getType(),
                        account.getName(),
                        account.getBalance()))
            .toList();
    logger.info("Retrieved {} account summaries for userId: {}", summaries.size(), userId);
    return summaries;
  }

  /**
   * Retrieves the net worth of a user, including total net worth and a breakdown by account.
   *
   * @param userId the ID of the user
   * @return a NetworthResponse containing total net worth and breakdown items
   * @throws IllegalArgumentException if userId is not positive
   */
  public NetworthResponse getNetworthResponse(long userId) {
    logger.debug("Fetching net worth for userId: {}", userId);
    Utils.validatePositiveId(userId, "User ID must be positive");
    BigDecimal totalNetWorth = getTotalBalance(userId);
    List<Account> accounts = accountRepo.findAccountsNameBalanceByUserId(userId);
    List<BreakdownItem> breakdownItemList =
        accounts.stream()
            .map(account -> new BreakdownItem(account.getName(), account.getBalance()))
            .toList();
    logger.info(
        "Net worth for userId {}: {} with {} breakdown items",
        userId,
        totalNetWorth,
        breakdownItemList.size());
    return new NetworthResponse(totalNetWorth, breakdownItemList);
  }

  /**
   * Generates spending insights for a user based on transactions from the last 30 days.
   *
   * @param userId the ID of the user
   * @return a list of spending insights, or a default message if no transactions exist
   * @throws IllegalArgumentException if userId is not positive
   */
  public List<SpendingInsight> getSpendingInsights(long userId) {
    logger.debug("Generating spending insights for userId: {}", userId);
    Utils.validatePositiveId(userId, "User ID must be positive");
    LocalDate endDate = LocalDate.now();
    LocalDate startDate = endDate.minusDays(SPENDING_INSIGHT_DAYS);
    List<Transaction> recentTransactions =
        transactionService.getTransactionsByUserIdInTimeFrame(userId, startDate, endDate);

    if (recentTransactions.isEmpty()) {
      logger.warn(
          "No transactions found for userId {} in the last {} days", userId, SPENDING_INSIGHT_DAYS);
      return List.of(
          new SpendingInsight(
              "Not enough transaction data for the last "
                  + SPENDING_INSIGHT_DAYS
                  + " days to generate insights."));
    }

    String transactionStats = createTransactionStats(recentTransactions);
    String prompt = aiInsightService.buildStrictPrompt(transactionStats);
    String rawInsights = aiInsightService.getInsightsFromText(prompt);
    List<SpendingInsight> insights = aiInsightService.parseInsightsToList(rawInsights);
    logger.info("Generated {} spending insights for userId: {}", insights.size(), userId);
    return insights;
  }

  /**
   * Creates a formatted string summarizing transactions by category for AI insight generation.
   *
   * @param transactions the list of transactions to summarize
   * @return a formatted string of spending by category
   */
  private String createTransactionStats(List<Transaction> transactions) {
    logger.debug("Creating transaction stats for {} transactions", transactions.size());
    Map<String, BigDecimal> spendingByCategory =
        transactions.stream()
            .filter(t -> t.getAmount() != null && t.getAmount().compareTo(BigDecimal.ZERO) < 0)
            .collect(
                Collectors.groupingBy(
                    Transaction::getCategory,
                    Collectors.reducing(BigDecimal.ZERO, Transaction::getAmount, BigDecimal::add)));

    StringBuilder summary =
        new StringBuilder("User's spending over the last " + SPENDING_INSIGHT_DAYS + " days:\n");
    spendingByCategory.entrySet().stream()
        .sorted(Map.Entry.comparingByKey())
        .forEach(
            entry ->
                summary.append(
                    String.format("- %s: $%.2f\n", entry.getKey(), entry.getValue().abs())));
    logger.debug("Transaction stats created: {}", summary);
    return summary.toString();
  }

  /**
   * Retrieves a summary of the top 3 saving goals for a user, ordered by target amount.
   *
   * @param userId the ID of the user
   * @return a list of saving goal summaries
   * @throws IllegalArgumentException if userId is not positive
   */
  public List<SavingGoalSummary> getSavingGoalSummary(long userId) {
    logger.debug("Fetching saving goal summary for userId: {}", userId);
    Utils.validatePositiveId(userId, "User ID must be positive");
    List<SavingGoal> goals = savingGoalsRepo.findTop3ByUser_IdOrderByTargetDesc(userId);
    List<SavingGoalSummary> summaries =
        goals.stream()
            .map(
                goal ->
                    new SavingGoalSummary(goal.getTitle(), goal.getContributed(), goal.getTarget()))
            .toList();
    logger.info("Retrieved {} saving goal summaries for userId: {}", summaries.size(), userId);
    return summaries;
  }

  /**
   * Retrieves income and expense summaries for the current and previous months.
   *
   * @param userId the ID of the user
   * @return a list of income and expense summaries for this month and last month
   * @throws IllegalArgumentException if userId is not positive
   */
  public List<IncomeExpenseSummary> getIncomeExpenseSummary(long userId) {
    logger.debug("Fetching income/expense summary for userId: {}", userId);
    Utils.validatePositiveId(userId, "User ID must be positive");
    LocalDate lastMonthStart = LocalDate.now().minusMonths(1).withDayOfMonth(1);
    LocalDate lastMonthEnd = lastMonthStart.withDayOfMonth(lastMonthStart.lengthOfMonth());

    BigDecimal incomeThisMonth =
        Optional.ofNullable(transactionRepo.getTotalCreditThisMonth(userId))
            .orElse(BigDecimal.ZERO);
    BigDecimal expensesThisMonth =
        Optional.ofNullable(transactionRepo.getTotalDebitThisMonth(userId)).orElse(BigDecimal.ZERO);
    BigDecimal incomeLastMonth =
        Optional.ofNullable(
                transactionRepo.getTotalCreditBetween(userId, lastMonthStart, lastMonthEnd))
            .orElse(BigDecimal.ZERO);
    BigDecimal expensesLastMonth =
        Optional.ofNullable(
                transactionRepo.getTotalDebitBetween(userId, lastMonthStart, lastMonthEnd))
            .orElse(BigDecimal.ZERO);

    IncomeExpenseSummary thisMonth =
        new IncomeExpenseSummary("This month", incomeThisMonth, expensesThisMonth);
    IncomeExpenseSummary lastMonth =
        new IncomeExpenseSummary("Last month", incomeLastMonth, expensesLastMonth);
    logger.info("Retrieved income/expense summaries for userId: {}", userId);
    return List.of(thisMonth, lastMonth);
  }

  /**
   * Retrieves the income trend for a user over the current and previous years.
   *
   * @param userId the ID of the user
   * @return an IncomeTrend containing monthly income data for the current and previous years
   * @throws IllegalArgumentException if userId is not positive
   */
  public IncomeTrend getIncomeTrend(Long userId) {
    logger.debug("Fetching income trend for userId: {}", userId);
    Utils.validatePositiveId(userId, "User ID must be positive");
    List<String> months =
        List.of("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec");
    int currentYear = Year.now().getValue();
    int lastYear = currentYear - 1;

    List<BigDecimal> incomeThisYear =
        fillMissingMonthsWithZero(transactionRepo.getMonthlyIncomeForYear(userId, currentYear));
    List<BigDecimal> incomeLastYear =
        fillMissingMonthsWithZero(transactionRepo.getMonthlyIncomeForYear(userId, lastYear));
    logger.info(
        "Retrieved income trend for userId: {} for years {} and {}", userId, currentYear, lastYear);
    return new IncomeTrend(months, incomeThisYear, incomeLastYear);
  }

  /**
   * Fills missing months with zero income for a yearly income data list.
   *
   * @param monthlyDataFromRepo the raw monthly data from the repository
   * @return a list of 12 BigDecimal values representing monthly income
   */
  private List<BigDecimal> fillMissingMonthsWithZero(List<Object[]> monthlyDataFromRepo) {
    logger.debug("Filling missing months for {} monthly data entries", monthlyDataFromRepo.size());
    List<BigDecimal> filledData = new ArrayList<>(Collections.nCopies(12, BigDecimal.ZERO));
    for (Object[] row : monthlyDataFromRepo) {
      Integer month = (Integer) row[0];
      BigDecimal income = (BigDecimal) row[1];
      if (month != null && month >= 1 && month <= 12) {
        filledData.set(month - 1, income != null ? income : BigDecimal.ZERO);
      }
    }
    logger.debug("Filled monthly data: {}", filledData);
    return filledData;
  }

  /**
   * Retrieves the top 5 expense categories by amount for a user.
   *
   * @param userId the ID of the user
   * @return a list of expense analysis data for the top 5 categories
   * @throws IllegalArgumentException if userId is not positive
   */
  public List<ExpenseAnalysis> getExpenseAnalysis(long userId) {
    logger.debug("Fetching expense analysis for userId: {}", userId);
    Utils.validatePositiveId(userId, "User ID must be positive");
    List<ExpenseAnalysis> analysis = transactionRepo.findTop5ExpenseCategoriesByAmount(userId);
    logger.info("Retrieved {} expense categories for userId: {}", analysis.size(), userId);
    return analysis;
  }

  /**
   * Retrieves the 3 most recent transactions for a user.
   *
   * @param userId the ID of the user
   * @return a list of recent transaction summaries
   * @throws IllegalArgumentException if userId is not positive
   */
  public List<RecentTransactions> getRecentTransactions(long userId) {
    logger.debug("Fetching recent transactions for userId: {}", userId);
    Utils.validatePositiveId(userId, "User ID must be positive");
    List<RecentTransactions> transactions = transactionRepo.findLatest3TransactionSummaries(userId);
    logger.info("Retrieved {} recent transactions for userId: {}", transactions.size(), userId);
    return transactions;
  }
}
