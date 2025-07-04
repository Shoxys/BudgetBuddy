package com.shoxys.budgetbuddy_backend.Repo;

import com.shoxys.budgetbuddy_backend.Entities.SavingGoal;
import com.shoxys.budgetbuddy_backend.Entities.User;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SavingGoalsRepo extends JpaRepository<SavingGoal,Long> {
    List<SavingGoal> findTop3ByUser_IdOrderByTargetDesc(Long userId);

    @Query("SELECT g FROM SavingGoal g WHERE g.target > g.contributed AND g.user = ?1")
    List<SavingGoal> findPendingSavingGoalsForUser(User user);

    @Query("SELECT g FROM SavingGoal g WHERE g.target = g.contributed AND g.user = ?1")
    List<SavingGoal> findCompletedSavingGoalsForUser(User user);

    String findTitleForSavingGoalByIdAndUser(long id, User user);

    @Modifying
    @Query("UPDATE SavingGoal SET contributed = contributed + ?3 WHERE user = ?1 AND id = ?2")
    void updateSavingGoalContribution(User user, long id, BigDecimal contributed);

    @Query("SELECT SUM(g.contributed), 0 FROM SavingGoal g WHERE g.user = :user")
    BigDecimal sumContributionsByUser(User user);

    int findCountCompletedSavingGoalsByUser(User user);

    @Query("SELECT COALESCE(COUNT(g)) FROM SavingGoal g WHERE g.target = g.contributed AND g.user = ?1 AND g.date BETWEEN ?2 AND ?3")
    int amountOfCompletedGoalsInWeek(User user, LocalDate startOfWeek, LocalDate endOfWeek);

    int findCountINPROGRESSSavingGoalsByUser(User user);

    @Query("SELECT COALESCE(COUNT(g)) FROM SavingGoal g WHERE g.target > g.contributed AND g.user = ?1 AND g.date BETWEEN ?2 AND ?3")
    int amountOfInProgressGoalsInWeek(User user, LocalDate startOfWeek, LocalDate endOfWeek);

    int findCountOVERDUESavingGoalsByUser(User user);

    @Query("SELECT COALESCE(COUNT(g)) FROM SavingGoal g WHERE g.user = ?1 AND g.date < CURDATE()")
    int amountOfOverdueGoalsInWeek(User user, LocalDate startOfWeek, LocalDate endOfWeek);

    int findCountSavingGoalsByUser(User user);

    void deleteSavingGoalByIdAndUser(long id, User user);

    Optional<SavingGoal> findSavingGoalByIdAndUser(long id, User user);


}
