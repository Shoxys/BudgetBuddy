package com.shoxys.budgetbuddy_backend.Controllers;

import com.shoxys.budgetbuddy_backend.Config.Constants;
import com.shoxys.budgetbuddy_backend.DTOs.Dashboard.*;
import com.shoxys.budgetbuddy_backend.Entities.User;
import com.shoxys.budgetbuddy_backend.Security.AppUserDetails;
import com.shoxys.budgetbuddy_backend.Services.DashboardService;
import com.shoxys.budgetbuddy_backend.Services.UserService;
import java.math.BigDecimal;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/** Handles HTTP requests for dashboard-related data and insights. */
@RestController
@RequestMapping(Constants.DASHBOARD_ENDPOINT)
public class DashboardController {
  private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);
  private final DashboardService dashboardService;
  private final UserService userService;

  public DashboardController(DashboardService dashboardService, UserService userService) {
    this.dashboardService = dashboardService;
    this.userService = userService;
  }

  /**
   * Retrieves the total balance across all accounts for the authenticated user.
   *
   * @param userDetails the authenticated user's details
   * @return the total balance
   */
  @GetMapping("/total-balance")
  public ResponseEntity<BigDecimal> getTotalBalance(
      @AuthenticationPrincipal AppUserDetails userDetails) {
    String username = validateUserDetails(userDetails);
    logger.info("Fetching total balance for user: {}", username);
    User user = userService.getUserByEmail(username);
    BigDecimal totalBalance = dashboardService.getTotalBalance(user.getId());
    logger.info("Total balance retrieved for user: {}", username);
    return ResponseEntity.ok(totalBalance);
  }

  /**
   * Retrieves a summary of all accounts for the authenticated user.
   *
   * @param userDetails the authenticated user's details
   * @return a list of account summaries
   */
  @GetMapping("/accounts-summary")
  public ResponseEntity<List<AccountSummary>> getAccountsSummary(
      @AuthenticationPrincipal AppUserDetails userDetails) {
    String username = validateUserDetails(userDetails);
    logger.info("Fetching accounts summary for user: {}", username);
    User user = userService.getUserByEmail(username);
    List<AccountSummary> summary = dashboardService.getAccountSummary(user.getId());
    logger.info("Accounts summary retrieved for user: {}, count: {}", username, summary.size());
    return ResponseEntity.ok(summary);
  }

  /**
   * Retrieves the net worth for the authenticated user.
   *
   * @param userDetails the authenticated user's details
   * @return the net worth response
   */
  @GetMapping("/networth")
  public ResponseEntity<NetworthResponse> getNetworth(
      @AuthenticationPrincipal AppUserDetails userDetails) {
    String username = validateUserDetails(userDetails);
    logger.info("Fetching net worth for user: {}", username);
    User user = userService.getUserByEmail(username);
    NetworthResponse networth = dashboardService.getNetworthResponse(user.getId());
    logger.info("Net worth retrieved for user: {}", username);
    return ResponseEntity.ok(networth);
  }

  /**
   * Retrieves spending insights for the authenticated user.
   *
   * @param userDetails the authenticated user's details
   * @return a list of spending insights
   */
  @GetMapping("/spending-insights")
  public ResponseEntity<List<SpendingInsight>> getSpendingInsights(
      @AuthenticationPrincipal AppUserDetails userDetails) {
    String username = validateUserDetails(userDetails);
    logger.info("Fetching spending insights for user: {}", username);
    User user = userService.getUserByEmail(username);
    List<SpendingInsight> insights = dashboardService.getSpendingInsights(user.getId());
    logger.info("Spending insights retrieved for user: {}, count: {}", username, insights.size());
    return ResponseEntity.ok(insights);
  }

  /**
   * Retrieves saving goals for the authenticated user.
   *
   * @param userDetails the authenticated user's details
   * @return a list of saving goal summaries
   */
  @GetMapping("/saving-goals")
  public ResponseEntity<List<SavingGoalSummary>> getSavingGoals(
      @AuthenticationPrincipal AppUserDetails userDetails) {
    String username = validateUserDetails(userDetails);
    logger.info("Fetching saving goals for user: {}", username);
    User user = userService.getUserByEmail(username);
    List<SavingGoalSummary> goals = dashboardService.getSavingGoalSummary(user.getId());
    logger.info("Saving goals retrieved for user: {}, count: {}", username, goals.size());
    return ResponseEntity.ok(goals);
  }

  /**
   * Retrieves income and expense summary for the authenticated user.
   *
   * @param userDetails the authenticated user's details
   * @return a list of income/expense summaries
   */
  @GetMapping("/income-expense-summary")
  public ResponseEntity<List<IncomeExpenseSummary>> getIncomeExpenseSummary(
      @AuthenticationPrincipal AppUserDetails userDetails) {
    String username = validateUserDetails(userDetails);
    logger.info("Fetching income/expense summary for user: {}", username);
    User user = userService.getUserByEmail(username);
    List<IncomeExpenseSummary> summary = dashboardService.getIncomeExpenseSummary(user.getId());
    logger.info(
        "Income/expense summary retrieved for user: {}, count: {}", username, summary.size());
    return ResponseEntity.ok(summary);
  }

  /**
   * Retrieves the income trend for the authenticated user.
   *
   * @param userDetails the authenticated user's details
   * @return the income trend
   */
  @GetMapping("/income-trend")
  public ResponseEntity<IncomeTrend> getIncomeTrend(
      @AuthenticationPrincipal AppUserDetails userDetails) {
    String username = validateUserDetails(userDetails);
    logger.info("Fetching income trend for user: {}", username);
    User user = userService.getUserByEmail(username);
    IncomeTrend trend = dashboardService.getIncomeTrend(user.getId());
    logger.info("Income trend retrieved for user: {}", username);
    return ResponseEntity.ok(trend);
  }

  /**
   * Retrieves expense analysis for the authenticated user.
   *
   * @param userDetails the authenticated user's details
   * @return a list of expense analyses
   */
  @GetMapping("/expense-analysis")
  public ResponseEntity<List<ExpenseAnalysis>> getExpenseAnalysis(
      @AuthenticationPrincipal AppUserDetails userDetails) {
    String username = validateUserDetails(userDetails);
    logger.info("Fetching expense analysis for user: {}", username);
    User user = userService.getUserByEmail(username);
    List<ExpenseAnalysis> analysis = dashboardService.getExpenseAnalysis(user.getId());
    logger.info("Expense analysis retrieved for user: {}, count: {}", username, analysis.size());
    return ResponseEntity.ok(analysis);
  }

  /**
   * Retrieves recent transactions for the authenticated user.
   *
   * @param userDetails the authenticated user's details
   * @return a list of recent transactions
   */
  @GetMapping("/recent-transactions")
  public ResponseEntity<List<RecentTransactions>> getRecentTransactions(
      @AuthenticationPrincipal AppUserDetails userDetails) {
    String username = validateUserDetails(userDetails);
    logger.info("Fetching recent transactions for user: {}", username);
    User user = userService.getUserByEmail(username);
    List<RecentTransactions> transactions = dashboardService.getRecentTransactions(user.getId());
    logger.info(
        "Recent transactions retrieved for user: {}, count: {}", username, transactions.size());
    return ResponseEntity.ok(transactions);
  }

  private String validateUserDetails(AppUserDetails userDetails) {
    if (userDetails == null) {
      logger.warn("Unauthorized dashboard request");
      throw new IllegalStateException("User is not authenticated");
    }
    return userDetails.getUsername();
  }
}
