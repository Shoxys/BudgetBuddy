package com.shoxys.budgetbuddy_backend.Services;

import com.shoxys.budgetbuddy_backend.DTOs.*;
import com.shoxys.budgetbuddy_backend.Entities.Account;
import com.shoxys.budgetbuddy_backend.Entities.SavingGoal;
import com.shoxys.budgetbuddy_backend.Entities.User;
import com.shoxys.budgetbuddy_backend.Enums.AccountType;
import com.shoxys.budgetbuddy_backend.Enums.GoalType;
import com.shoxys.budgetbuddy_backend.Exceptions.SavingGoalNotFoundException;
import com.shoxys.budgetbuddy_backend.Exceptions.UserNotFoundException;
import com.shoxys.budgetbuddy_backend.Repo.AccountRepo;
import com.shoxys.budgetbuddy_backend.Repo.SavingGoalsRepo;
import com.shoxys.budgetbuddy_backend.Repo.UserRepo;
import com.shoxys.budgetbuddy_backend.Utils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Service
public class SavingGoalService {
    @Autowired
    SavingGoalsRepo savingGoalsRepo;
    @Autowired
    AccountRepo accountRepo;
    @Autowired
    UserRepo userRepo;
    @Autowired
    private AccountService accountService;

    public String getSavingGoalTitleById(String email, long id) {
        User user = userRepo.getUserByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        return savingGoalsRepo.findTitleForSavingGoalByIdAndUser(id, user);
    }

    public BigDecimal getTotalContributionForUser(String email) {
        User user = userRepo.getUserByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        return savingGoalsRepo.sumContributionsByUser(user);
    }

    public List<SavingGoal> getPendingSavingGoalsForUser(String email){
        User user = userRepo.getUserByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        return savingGoalsRepo.findPendingSavingGoalsForUser(user);
    }

    public List<SavingGoal> getCompleteSavingGoalsForUser(String email){
        User user = userRepo.getUserByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        return savingGoalsRepo.findCompletedSavingGoalsForUser(user);
    }

    public GoalStatsResponse getGoalStatsForUser(String email) {
        User user = userRepo.getUserByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        List<GoalStat> goalStatList = Arrays.asList(getCompletedGoalStat(user),getInProgressGoalStat(user),
                getOverdueGoalStat(user), getTotalGoatStat(user));

        return new GoalStatsResponse(goalStatList);
    }

    public GoalStat getCompletedGoalStat(User user) {
        int completedCount = savingGoalsRepo.findCountCompletedSavingGoalsByUser(user);

        int completedInWeek = savingGoalsRepo.amountOfCompletedGoalsInWeek(user, Utils.getStartOfWeek(), Utils.getEndOfWeek());
        String plurality = completedInWeek > 1 ? "goals" : "goal";
        String completedGoalInsight = String.format("You have completed %d %s this week", completedInWeek, plurality);
        return new GoalStat(completedGoalInsight, GoalType.COMPLETED, completedCount);
    }

    public GoalStat getInProgressGoalStat(User user) {
        int inProgressCount = savingGoalsRepo.findCountINPROGRESSSavingGoalsByUser(user);

        int inProgressInWeek = savingGoalsRepo.amountOfInProgressGoalsInWeek(user, Utils.getStartOfWeek(), Utils.getEndOfWeek());
        String plurality = inProgressInWeek > 1 ? "goals" : "goal";
        String inProgressGoalInsight = String.format("You have %d in progress %s this week", inProgressInWeek, plurality);

        return new GoalStat(inProgressGoalInsight, GoalType.INPROGRESS, inProgressCount);
    }

    public GoalStat getOverdueGoalStat(User user) {
        int overdueCount = savingGoalsRepo.findCountOVERDUESavingGoalsByUser(user);
        int overdueInWeek = savingGoalsRepo.amountOfOverdueGoalsInWeek(user, Utils.getStartOfWeek(), Utils.getEndOfWeek());
        String plurality = overdueInWeek > 1 ? "goals" : "goal";
        String overdueGoalInsight = String.format("You have %d overdue %s this week", overdueInWeek, plurality);

        return new GoalStat(overdueGoalInsight, GoalType.OVERDUE, overdueCount);
    }

    public GoalStat getTotalGoatStat(User user) {
        int totalCount = savingGoalsRepo.findCountSavingGoalsByUser(user);
        int CompletedCount = savingGoalsRepo.findCountCompletedSavingGoalsByUser(user);

        int completionPercent = (int) ((double)CompletedCount / totalCount * 100);
        String completionPercentInsight = String.format("You have completed %s%% of total goals", completionPercent);

        return new GoalStat(completionPercentInsight, GoalType.TOTAL, totalCount);
    }

    @Transactional
    public void updateContributionForSavingGoal (String email, long id, GoalContributionRequest request) {
        User user = userRepo.getUserByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        savingGoalsRepo.updateSavingGoalContribution(user, id, request.getContribution());
        //Recalculate account balance
        accountService.recalculateGoalSavingsBalance(user);
    }

    @Transactional
    public SavingGoal createSavingGoal(String email, SavingGoalRequest request) {
        User user = userRepo.getUserByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Account savingGoalsAccount = accountRepo.findAccountByUserAndType(user, AccountType.GOALSAVINGS)
                .orElseGet(()-> accountService.createGoalSavingsAccount(user, request.getContributed()));

        SavingGoal newSavingGoal = new SavingGoal(
                request.getTitle(),
                request.getTarget(),
                request.getContributed(),
                request.getDate(),
                request.getImageRef(),
                savingGoalsAccount,
                user
        );
        SavingGoal savedGoal = savingGoalsRepo.save(newSavingGoal);
        //Recalculate account balance
        accountService.recalculateGoalSavingsBalance(user);
        return savedGoal;
    }

    @Transactional
    public SavingGoal updateSavingGoal(String email, Long id, SavingGoalRequest request) {
        User user = userRepo.getUserByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        SavingGoal savingGoal = savingGoalsRepo.findSavingGoalByIdAndUser(id, user)
                .orElseThrow(() -> new SavingGoalNotFoundException(id));

        savingGoal.setId(id); // Ensure ID matches
        savingGoal.setTitle(request.getTitle());
        savingGoal.setTarget(request.getTarget());
        savingGoal.setContributed(request.getContributed());
        savingGoal.setDate(request.getDate());
        savingGoal.setImageRef(request.getImageRef());

        SavingGoal updated = savingGoalsRepo.save(savingGoal);
        //Recalculate account balance
        accountService.recalculateGoalSavingsBalance(user);
        return updated;
    }

    @Transactional
    public void deleteSavingGoal(String email, long id) {
        User user = userRepo.getUserByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        savingGoalsRepo.deleteSavingGoalByIdAndUser(id, user);
        // Recalculate account balance
        accountService.recalculateGoalSavingsBalance(user);
    }
}
