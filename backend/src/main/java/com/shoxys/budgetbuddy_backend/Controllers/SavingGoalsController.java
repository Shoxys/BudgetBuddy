package com.shoxys.budgetbuddy_backend.Controllers;

import com.shoxys.budgetbuddy_backend.DTOs.GoalContributionRequest;
import com.shoxys.budgetbuddy_backend.DTOs.GoalStatsResponse;
import com.shoxys.budgetbuddy_backend.DTOs.SavingGoalRequest;
import com.shoxys.budgetbuddy_backend.Entities.SavingGoal;
import com.shoxys.budgetbuddy_backend.Security.AppUserDetails;
import com.shoxys.budgetbuddy_backend.Services.SavingGoalService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/saving-goals")
public class SavingGoalsController {
    private final SavingGoalService savingGoalService;

    public SavingGoalsController(SavingGoalService savingGoalService) {
        this.savingGoalService = savingGoalService;
    }

    @GetMapping("/pending")
    public List<SavingGoal> getPendingGoals(@AuthenticationPrincipal AppUserDetails userDetails) {
        return savingGoalService.getPendingSavingGoalsForUser(userDetails.getUsername());
    }

    @GetMapping("/completed")
    public List<SavingGoal> getCompleteGoals(@AuthenticationPrincipal AppUserDetails userDetails) {
        return savingGoalService.getCompleteSavingGoalsForUser(userDetails.getUsername());
    }

    @GetMapping("/{id}/title")
    public String getGoalTitle(@AuthenticationPrincipal AppUserDetails userDetails, @PathVariable long id) {
        return savingGoalService.getSavingGoalTitleById(userDetails.getUsername(), id);
    }

    @GetMapping("/stats")
    public GoalStatsResponse getGoalStats(@AuthenticationPrincipal AppUserDetails userDetails) {
        return savingGoalService.getGoalStatsForUser(userDetails.getUsername());
    }

    @PutMapping("/{id}/contribute")
    public ResponseEntity<?> updateContributionForGoal(
            @AuthenticationPrincipal AppUserDetails userDetails,
            @PathVariable long id,
            @Valid @RequestBody GoalContributionRequest request) {
        savingGoalService.updateContributionForSavingGoal(userDetails.getUsername(), id, request);
        return ResponseEntity.ok("Goal contribution successfully updated");
    }

    @PostMapping
    public SavingGoal createNewGoal(
            @AuthenticationPrincipal AppUserDetails userDetails,
            @Valid @RequestBody SavingGoalRequest request) {
        return savingGoalService.createSavingGoal(userDetails.getUsername(), request);
    }

    @PutMapping("/{id}/update")
    public SavingGoal updateGoal(
            @AuthenticationPrincipal AppUserDetails userDetails,
            @PathVariable long id,
            @Valid @RequestBody SavingGoalRequest request) {
        return savingGoalService.updateSavingGoal(userDetails.getUsername(), id, request);
    }

    @DeleteMapping("/{id}/delete")
    public void deleteGoal(@AuthenticationPrincipal AppUserDetails userDetails, @PathVariable long id) {
        savingGoalService.deleteSavingGoal(userDetails.getUsername(), id);
    }

}
