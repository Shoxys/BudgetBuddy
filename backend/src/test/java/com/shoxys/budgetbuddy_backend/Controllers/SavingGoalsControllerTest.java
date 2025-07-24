package com.shoxys.budgetbuddy_backend.Controllers;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shoxys.budgetbuddy_backend.DTOs.SavingGoal.GoalContributionRequest;
import com.shoxys.budgetbuddy_backend.DTOs.SavingGoal.GoalStat;
import com.shoxys.budgetbuddy_backend.DTOs.SavingGoal.GoalStatsResponse;
import com.shoxys.budgetbuddy_backend.DTOs.SavingGoal.SavingGoalRequest;
import com.shoxys.budgetbuddy_backend.Entities.SavingGoal;
import com.shoxys.budgetbuddy_backend.Entities.User;
import com.shoxys.budgetbuddy_backend.Enums.GoalType;
import com.shoxys.budgetbuddy_backend.Security.AppUserDetails;
import com.shoxys.budgetbuddy_backend.Services.SavingGoalService;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(
    value = SavingGoalsController.class,
    excludeFilters =
        @Filter(
            type = FilterType.ASSIGNABLE_TYPE,
            classes = com.shoxys.budgetbuddy_backend.Security.JwtAuthFilter.class))
@AutoConfigureMockMvc(addFilters = false)
public class SavingGoalsControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private SavingGoalService savingGoalService;

  @Autowired private ObjectMapper objectMapper;

  private final String testEmail = "test@example.com";
  private AppUserDetails principal;

  @BeforeEach
  void setUp() {
    // Setup AppUserDetails
    User mockUser = new User(testEmail, "testPassword");
    principal = new AppUserDetails(mockUser);

    // Set up the security context
    SecurityContext context = SecurityContextHolder.createEmptyContext();
    context.setAuthentication(
        new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));
    SecurityContextHolder.setContext(context);
  }

  @Test
  void testGetPendingGoals_Returns200AndGoalsList() throws Exception {
    // Arrange
    SavingGoal goal = new SavingGoal();
    goal.setId(1L);
    goal.setTitle("Vacation Fund");
    List<SavingGoal> goals = Collections.singletonList(goal);
    when(savingGoalService.getPendingSavingGoalsForUser(eq(testEmail))).thenReturn(goals);

    // Act & Assert
    mockMvc
        .perform(get("/api/saving-goals/pending").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$[0].id").value(1))
        .andExpect(jsonPath("$[0].title").value("Vacation Fund"));

    verify(savingGoalService, times(1)).getPendingSavingGoalsForUser(eq(testEmail));
  }

  @Test
  void testGetGoalTitle_Returns200AndTitle() throws Exception {
    // Arrange
    long goalId = 1L;
    String title = "Vacation Fund";
    when(savingGoalService.getSavingGoalTitleById(eq(testEmail), eq(goalId))).thenReturn(title);

    // Act & Assert
    mockMvc
        .perform(
            get("/api/saving-goals/{id}/title", goalId).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
        .andExpect(content().string("Vacation Fund"));

    verify(savingGoalService, times(1)).getSavingGoalTitleById(eq(testEmail), eq(goalId));
  }

  @Test
  void testGetGoalStats_Returns200AndStats() throws Exception {
    // Arrange
    List<GoalStat> goalStats =
        List.of(
            new GoalStat("20% Completed", GoalType.TOTAL, 4),
            new GoalStat("10% Overdue", GoalType.OVERDUE, 2));
    GoalStatsResponse stats = new GoalStatsResponse(goalStats);
    when(savingGoalService.getGoalStatsForUser(eq(testEmail))).thenReturn(stats);

    // Act & Assert
    mockMvc
        .perform(get("/api/saving-goals/stats").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.goalStats[0].insight").value("20% Completed"))
        .andExpect(jsonPath("$.goalStats[0].goalType").value("TOTAL"))
        .andExpect(jsonPath("$.goalStats[0].amount").value(4))
        .andExpect(jsonPath("$.goalStats[1].insight").value("10% Overdue"))
        .andExpect(jsonPath("$.goalStats[1].goalType").value("OVERDUE"))
        .andExpect(jsonPath("$.goalStats[1].amount").value(2));

    verify(savingGoalService, times(1)).getGoalStatsForUser(eq(testEmail));
  }

  @Test
  void testUpdateContributionForGoal_Returns200AndMessage() throws Exception {
    // Arrange
    long goalId = 1L;
    GoalContributionRequest request = new GoalContributionRequest();
    request.setContribution(BigDecimal.valueOf(100));
    doNothing()
        .when(savingGoalService)
        .updateContributionForSavingGoal(
            eq(testEmail), eq(goalId), any(GoalContributionRequest.class));

    // Act & Assert
    mockMvc
        .perform(
            put("/api/saving-goals/{id}/contribute", goalId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
        .andExpect(content().string("Goal contribution successfully updated"));

    verify(savingGoalService, times(1))
        .updateContributionForSavingGoal(
            eq(testEmail), eq(goalId), any(GoalContributionRequest.class));
  }

  @Test
  void testCreateNewGoal_Returns200AndGoal() throws Exception {
    // Arrange
    SavingGoalRequest request = new SavingGoalRequest();
    request.setTitle("New Car");
    request.setTarget(BigDecimal.valueOf(20000));
    SavingGoal goal = new SavingGoal();
    goal.setId(1L);
    goal.setTitle("New Car");
    when(savingGoalService.createSavingGoal(eq(testEmail), any(SavingGoalRequest.class)))
        .thenReturn(goal);

    // Act & Assert
    mockMvc
        .perform(
            post("/api/saving-goals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.title").value("New Car"));

    verify(savingGoalService, times(1))
        .createSavingGoal(eq(testEmail), any(SavingGoalRequest.class));
  }

  @Test
  void testUpdateGoal_Returns200AndUpdatedGoal() throws Exception {
    // Arrange
    long goalId = 1L;
    SavingGoalRequest request = new SavingGoalRequest();
    request.setTitle("Updated Car Fund");
    SavingGoal goal = new SavingGoal();
    goal.setId(goalId);
    goal.setTitle("Updated Car Fund");
    when(savingGoalService.updateSavingGoal(
            eq(testEmail), eq(goalId), any(SavingGoalRequest.class)))
        .thenReturn(goal);

    // Act & Assert
    mockMvc
        .perform(
            put("/api/saving-goals/{id}/update", goalId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value(goalId))
        .andExpect(jsonPath("$.title").value("Updated Car Fund"));

    verify(savingGoalService, times(1))
        .updateSavingGoal(eq(testEmail), eq(goalId), any(SavingGoalRequest.class));
  }

  @Test
  void testDeleteGoal_Returns200() throws Exception {
    // Arrange
    long goalId = 1L;
    doNothing().when(savingGoalService).deleteSavingGoal(eq(testEmail), eq(goalId));

    // Act & Assert
    mockMvc.perform(delete("/api/saving-goals/{id}/delete", goalId)).andExpect(status().isOk());

    verify(savingGoalService, times(1)).deleteSavingGoal(eq(testEmail), eq(goalId));
  }
}
