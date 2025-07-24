package com.shoxys.budgetbuddy_backend.Controllers;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shoxys.budgetbuddy_backend.DTOs.Dashboard.*;
import com.shoxys.budgetbuddy_backend.Entities.User;
import com.shoxys.budgetbuddy_backend.Enums.AccountType;
import com.shoxys.budgetbuddy_backend.Security.AppUserDetails;
import com.shoxys.budgetbuddy_backend.Services.DashboardService;
import com.shoxys.budgetbuddy_backend.Services.UserService;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(
    value = DashboardController.class,
    excludeFilters = {
      @ComponentScan.Filter(
          type = FilterType.ASSIGNABLE_TYPE,
          classes = com.shoxys.budgetbuddy_backend.Security.JwtAuthFilter.class)
    })
@AutoConfigureMockMvc(addFilters = false)
class DashboardControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private DashboardService dashboardService;

  @MockitoBean private UserService userService;

  @Autowired private ObjectMapper objectMapper;

  private User testUser;
  private AppUserDetails testUserDetails;

  @BeforeEach
  void setUp() {
    // Create a test user
    testUser = new User();
    testUser.setId(1L);
    testUser.setEmail("test@example.com");
    testUser.setHashedPassword("password");

    // Create test user details for authentication principal
    testUserDetails = new AppUserDetails(testUser);
    AppUserDetails principal = new AppUserDetails(testUser);
    SecurityContext context = SecurityContextHolder.createEmptyContext();
    context.setAuthentication(
        new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));
    SecurityContextHolder.setContext(context);
  }

  @Test
  void getTotalBalance_ShouldReturnTotalBalance() throws Exception {
    // Mocking the service layer
    BigDecimal expectedBalance = new BigDecimal("15000.75");
    when(userService.getUserByEmail(anyString())).thenReturn(testUser);
    when(dashboardService.getTotalBalance(anyLong())).thenReturn(expectedBalance);

    // Performing the request and asserting the response
    mockMvc
        .perform(get("/api/dashboard/total-balance").with(user(testUserDetails)))
        .andExpect(status().isOk())
        .andExpect(content().string(expectedBalance.toString()));
  }

  @Test
  void getAccountsSummary_ShouldReturnAccountSummaries() throws Exception {
    // Mocking the service layer
    List<AccountSummary> expectedSummary =
        Collections.singletonList(
            new AccountSummary(
                1, AccountType.SPENDING, "Saving Account", new BigDecimal("10000.00")));
    when(userService.getUserByEmail(anyString())).thenReturn(testUser);
    when(dashboardService.getAccountSummary(anyLong())).thenReturn(expectedSummary);

    // Performing the request and asserting the response
    mockMvc
        .perform(get("/api/dashboard/accounts-summary").with(user(testUserDetails)))
        .andExpect(status().isOk())
        .andExpect(content().json(objectMapper.writeValueAsString(expectedSummary)));
  }

  @Test
  void getNetworth_ShouldReturnNetworthResponse() throws Exception {
    // Mocking the service layer
    NetworthResponse expectedNetworth =
        new NetworthResponse(
            new BigDecimal("50000.00"),
            Collections.nCopies(5, new BreakdownItem("Savings", BigDecimal.valueOf(10000))));
    when(userService.getUserByEmail(anyString())).thenReturn(testUser);
    when(dashboardService.getNetworthResponse(anyLong())).thenReturn(expectedNetworth);

    // Performing the request and asserting the response
    mockMvc
        .perform(get("/api/dashboard/networth").with(user(testUserDetails)))
        .andExpect(status().isOk())
        .andExpect(content().json(objectMapper.writeValueAsString(expectedNetworth)));
  }

  @Test
  void getSpendingInsights_ShouldReturnSpendingInsights() throws Exception {
    // Mocking the service layer
    List<SpendingInsight> expectedInsights =
        Collections.singletonList(new SpendingInsight("Cut down on eating out"));
    when(userService.getUserByEmail(anyString())).thenReturn(testUser);
    when(dashboardService.getSpendingInsights(anyLong())).thenReturn(expectedInsights);

    // Performing the request and asserting the response
    mockMvc
        .perform(get("/api/dashboard/spending-insights").with(user(testUserDetails)))
        .andExpect(status().isOk())
        .andExpect(content().json(objectMapper.writeValueAsString(expectedInsights)));
  }

  @Test
  void getSavingGoals_ShouldReturnSavingGoalSummaries() throws Exception {
    // Mocking the service layer
    List<SavingGoalSummary> expectedGoals =
        Collections.singletonList(
            new SavingGoalSummary("Vacation", new BigDecimal("2000.00"), new BigDecimal("500.00")));
    when(userService.getUserByEmail(anyString())).thenReturn(testUser);
    when(dashboardService.getSavingGoalSummary(anyLong())).thenReturn(expectedGoals);

    // Performing the request and asserting the response
    mockMvc
        .perform(get("/api/dashboard/saving-goals").with(user(testUserDetails)))
        .andExpect(status().isOk())
        .andExpect(content().json(objectMapper.writeValueAsString(expectedGoals)));
  }

  @Test
  void getIncomeExpenseSummary_ShouldReturnIncomeExpenseSummaries() throws Exception {
    // Mocking the service layer
    List<IncomeExpenseSummary> expectedSummary =
        Collections.singletonList(
            new IncomeExpenseSummary("July", new BigDecimal("3000.00"), new BigDecimal("1500.00")));
    when(userService.getUserByEmail(anyString())).thenReturn(testUser);
    when(dashboardService.getIncomeExpenseSummary(anyLong())).thenReturn(expectedSummary);

    // Performing the request and asserting the response
    mockMvc
        .perform(get("/api/dashboard/income-expense-summary").with(user(testUserDetails)))
        .andExpect(status().isOk())
        .andExpect(content().json(objectMapper.writeValueAsString(expectedSummary)));
  }

  @Test
  void getIncomeTrend_ShouldReturnIncomeTrend() throws Exception {
    // Mocking the service layer
    IncomeTrend expectedTrend =
        new IncomeTrend(
            Collections.singletonList("Jan"),
            Collections.singletonList(new BigDecimal("3000")),
            Collections.singletonList(new BigDecimal("3000")));
    when(userService.getUserByEmail(anyString())).thenReturn(testUser);
    when(dashboardService.getIncomeTrend(anyLong())).thenReturn(expectedTrend);

    // Performing the request and asserting the response
    mockMvc
        .perform(get("/api/dashboard/income-trend").with(user(testUserDetails)))
        .andExpect(status().isOk())
        .andExpect(content().json(objectMapper.writeValueAsString(expectedTrend)));
  }

  @Test
  void getExpenseAnalysis_ShouldReturnExpenseAnalyses() throws Exception {
    // Mocking the service layer
    List<ExpenseAnalysis> expectedAnalysis =
        Collections.singletonList(new ExpenseAnalysis("Food", new BigDecimal("400.00")));
    when(userService.getUserByEmail(anyString())).thenReturn(testUser);
    when(dashboardService.getExpenseAnalysis(anyLong())).thenReturn(expectedAnalysis);

    // Performing the request and asserting the response
    mockMvc
        .perform(get("/api/dashboard/expense-analysis").with(user(testUserDetails)))
        .andExpect(status().isOk())
        .andExpect(content().json(objectMapper.writeValueAsString(expectedAnalysis)));
  }

  @Test
  void getRecentTransactions_ShouldReturnRecentTransactions() throws Exception {
    // Mocking the service layer
    List<RecentTransactions> expectedTransactions =
        Collections.singletonList(
            new RecentTransactions(
                new Date(2025, 05, 01), "Starbucks", "Shopping", new BigDecimal("-5.75")));
    when(userService.getUserByEmail(anyString())).thenReturn(testUser);
    when(dashboardService.getRecentTransactions(anyLong())).thenReturn(expectedTransactions);

    // Performing the request and asserting the response
    mockMvc
        .perform(get("/api/dashboard/recent-transactions").with(user(testUserDetails)))
        .andExpect(status().isOk())
        .andExpect(content().json(objectMapper.writeValueAsString(expectedTransactions)));
  }
}
