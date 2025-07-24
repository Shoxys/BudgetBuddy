package com.shoxys.budgetbuddy_backend.Controllers;

import com.shoxys.budgetbuddy_backend.Config.Constants;
import com.shoxys.budgetbuddy_backend.DTOs.SavingGoal.GoalContributionRequest;
import com.shoxys.budgetbuddy_backend.DTOs.SavingGoal.GoalStatsResponse;
import com.shoxys.budgetbuddy_backend.DTOs.SavingGoal.SavingGoalRequest;
import com.shoxys.budgetbuddy_backend.Entities.SavingGoal;
import com.shoxys.budgetbuddy_backend.Security.AppUserDetails;
import com.shoxys.budgetbuddy_backend.Services.SavingGoalService;
import jakarta.validation.Valid;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/** Handles HTTP requests for managing saving goals. */
@RestController
@RequestMapping(Constants.SAVING_GOALS_ENDPOINT)
public class SavingGoalsController {
  private static final Logger logger = LoggerFactory.getLogger(SavingGoalsController.class);
  private final SavingGoalService savingGoalService;

  public SavingGoalsController(SavingGoalService savingGoalService) {
    this.savingGoalService = savingGoalService;
  }

  /**
   * Retrieves pending saving goals for the authenticated user.
   *
   * @param userDetails the authenticated user's details
   * @return a list of pending saving goals
   */
  @GetMapping("/pending")
  public ResponseEntity<List<SavingGoal>> getPendingGoals(
      @AuthenticationPrincipal AppUserDetails userDetails) {
    String username = validateUserDetails(userDetails);
    logger.info("Fetching pending goals for user: {}", username);
    List<SavingGoal> goals = savingGoalService.getPendingSavingGoalsForUser(username);
    logger.info("Retrieved {} pending goals for user: {}", goals.size(), username);
    return ResponseEntity.ok(goals);
  }

  /**
   * Retrieves completed saving goals for the authenticated user.
   *
   * @param userDetails the authenticated user's details
   * @return a list of completed saving goals
   */
  @GetMapping("/completed")
  public ResponseEntity<List<SavingGoal>> getCompleteGoals(
      @AuthenticationPrincipal AppUserDetails userDetails) {
    String username = validateUserDetails(userDetails);
    logger.info("Fetching completed goals for user: {}", username);
    List<SavingGoal> goals = savingGoalService.getCompleteSavingGoalsForUser(username);
    logger.info("Retrieved {} completed goals for user: {}", goals.size(), username);
    return ResponseEntity.ok(goals);
  }

  /**
   * Retrieves the title of a saving goal by ID for the authenticated user.
   *
   * @param userDetails the authenticated user's details
   * @param id the saving goal ID
   * @return the goal title
   */
  @GetMapping("/{id}/title")
  public ResponseEntity<String> getGoalTitle(
      @AuthenticationPrincipal AppUserDetails userDetails, @PathVariable long id) {
    String username = validateUserDetails(userDetails);
    logger.info("Fetching goal title for user: {}, goal ID: {}", username, id);
    String title = savingGoalService.getSavingGoalTitleById(username, id);
    logger.info("Retrieved goal title for user: {}, goal ID: {}", username, id);
    return ResponseEntity.ok(title);
  }

  /**
   * Retrieves a saving goal by ID for the authenticated user.
   *
   * @param userDetails the authenticated user's details
   * @param id the saving goal ID
   * @return the saving goal
   */
  @GetMapping("/{id}")
  public ResponseEntity<SavingGoal> getGoalById(
      @AuthenticationPrincipal AppUserDetails userDetails, @PathVariable long id) {
    String username = validateUserDetails(userDetails);
    logger.info("Fetching goal for user: {}, goal ID: {}", username, id);
    SavingGoal goal = savingGoalService.getSavingGoalById(username, id);
    logger.info("Retrieved goal for user: {}, goal ID: {}", username, id);
    return ResponseEntity.ok(goal);
  }

  /**
   * Retrieves statistics for the authenticated user's saving goals.
   *
   * @param userDetails the authenticated user's details
   * @return the goal statistics
   */
  @GetMapping("/stats")
  public ResponseEntity<GoalStatsResponse> getGoalStats(
      @AuthenticationPrincipal AppUserDetails userDetails) {
    String username = validateUserDetails(userDetails);
    logger.info("Fetching goal stats for user: {}", username);
    GoalStatsResponse stats = savingGoalService.getGoalStatsForUser(username);
    logger.info("Retrieved goal stats for user: {}", username);
    return ResponseEntity.ok(stats);
  }

  /**
   * Updates the contribution for a saving goal.
   *
   * @param userDetails the authenticated user's details
   * @param id the saving goal ID
   * @param request the contribution request
   * @return a response indicating success
   */
  @PutMapping("/{id}/contribute")
  public ResponseEntity<String> updateContributionForGoal(
      @AuthenticationPrincipal AppUserDetails userDetails,
      @PathVariable long id,
      @Valid @RequestBody GoalContributionRequest request) {
    String username = validateUserDetails(userDetails);
    logger.info("Updating contribution for user: {}, goal ID: {}", username, id);
    savingGoalService.updateContributionForSavingGoal(username, id, request);
    logger.info("Contribution updated for user: {}, goal ID: {}", username, id);
    return ResponseEntity.ok("Goal contribution successfully updated");
  }

  /**
   * Creates a new saving goal for the authenticated user.
   *
   * @param userDetails the authenticated user's details
   * @param request the saving goal request
   * @return the created saving goal
   */
  @PostMapping
  public ResponseEntity<SavingGoal> createNewGoal(
      @AuthenticationPrincipal AppUserDetails userDetails,
      @Valid @RequestBody SavingGoalRequest request) {
    String username = validateUserDetails(userDetails);
    logger.info("Creating new goal for user: {}", username);
    SavingGoal goal = savingGoalService.createSavingGoal(username, request);
    logger.info("Created goal for user: {}, goal ID: {}", username, goal.getId());
    return ResponseEntity.ok(goal);
  }

  /**
   * Updates an existing saving goal for the authenticated user.
   *
   * @param userDetails the authenticated user's details
   * @param id the saving goal ID
   * @param request the saving goal request
   * @return the updated saving goal
   */
  @PutMapping("/{id}/update")
  public ResponseEntity<SavingGoal> updateGoal(
      @AuthenticationPrincipal AppUserDetails userDetails,
      @PathVariable long id,
      @Valid @RequestBody SavingGoalRequest request) {
    String username = validateUserDetails(userDetails);
    logger.info("Updating goal for user: {}, goal ID: {}", username, id);
    SavingGoal goal = savingGoalService.updateSavingGoal(username, id, request);
    logger.info("Updated goal for user: {}, goal ID: {}", username, id);
    return ResponseEntity.ok(goal);
  }

  /**
   * Deletes a saving goal for the authenticated user.
   *
   * @param userDetails the authenticated user's details
   * @param id the saving goal ID
   */
  @DeleteMapping("/{id}/delete")
  public ResponseEntity<Void> deleteGoal(
      @AuthenticationPrincipal AppUserDetails userDetails, @PathVariable long id) {
    String username = validateUserDetails(userDetails);
    logger.info("Deleting goal for user: {}, goal ID: {}", username, id);
    savingGoalService.deleteSavingGoal(username, id);
    logger.info("Deleted goal for user: {}, goal ID: {}", username, id);
    return ResponseEntity.ok().build();
  }

  private String validateUserDetails(AppUserDetails userDetails) {
    if (userDetails == null) {
      logger.warn("Unauthorized saving goals request");
      throw new IllegalStateException("User is not authenticated");
    }
    return userDetails.getUsername();
  }
}
