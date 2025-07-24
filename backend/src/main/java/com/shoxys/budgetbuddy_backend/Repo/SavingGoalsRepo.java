package com.shoxys.budgetbuddy_backend.Repo;

import com.shoxys.budgetbuddy_backend.Entities.SavingGoal;
import com.shoxys.budgetbuddy_backend.Entities.User;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Repository interface for managing {@link SavingGoal} entities, providing CRUD operations and
 * custom queries for retrieving and updating saving goals based on user, contributions, and status.
 */
public interface SavingGoalsRepo extends JpaRepository<SavingGoal, Long> {

  /**
   * Retrieves the top 3 saving goals for a user, ordered by target amount in descending order.
   *
   * @param userId the ID of the user
   * @return a list of up to 3 saving goals, or an empty list if none exist
   */
  List<SavingGoal> findTop3ByUser_IdOrderByTargetDesc(@Param("userId") Long userId);

  /**
   * Retrieves the title of a saving goal by its ID and associated user.
   *
   * @param id the saving goal ID
   * @param user the user entity
   * @return the title of the saving goal, or null if not found
   */
  String findTitleForSavingGoalByIdAndUser(@Param("id") long id, @Param("user") User user);

  /**
   * Deletes a saving goal by its ID and associated user.
   *
   * @param id the saving goal ID
   * @param user the user entity
   */
  void deleteSavingGoalByIdAndUser(@Param("id") long id, @Param("user") User user);

  /**
   * Finds a saving goal by its ID and associated user.
   *
   * @param id the saving goal ID
   * @param user the user entity
   * @return an {@code Optional} containing the saving goal, or empty if not found
   */
  Optional<SavingGoal> findSavingGoalByIdAndUser(@Param("id") long id, @Param("user") User user);

  /**
   * Updates the contributed amount for a saving goal by adding the specified amount.
   *
   * @param user the user entity
   * @param goalId the saving goal ID
   * @param contributed the amount to add to the existing contribution
   */
  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Transactional
  @Query(
      "UPDATE SavingGoal sg SET sg.contributed = COALESCE(sg.contributed, 0) + ?3 WHERE sg.user = ?1 AND sg.id = ?2")
  void updateSavingGoalContribution(
      @Param("user") User user,
      @Param("goalId") Long goalId,
      @Param("contributed") BigDecimal contributed);

  /**
   * Calculates the total contributed amount across all saving goals for a user.
   *
   * @param user the user entity
   * @return the sum of contributions, or 0 if none exist
   */
  @Query("SELECT SUM(g.contributed), 0 FROM SavingGoal g WHERE g.user = :user")
  BigDecimal sumContributionsByUser(@Param("user") User user);

  /**
   * Counts the total number of saving goals for a user.
   *
   * @param user the user entity
   * @return the number of saving goals
   */
  @Query("SELECT COUNT(g) FROM SavingGoal g WHERE g.user = ?1")
  int findCountSavingGoalsByUser(@Param("user") User user);

  /**
   * Counts the number of in-progress saving goals (target not yet reached) for a user.
   *
   * @param user the user entity
   * @return the number of in-progress saving goals
   */
  @Query("SELECT COUNT(g) FROM SavingGoal g WHERE g.target > g.contributed AND g.user = ?1")
  int findCountInProgressSavingGoalsByUser(@Param("user") User user);

  /**
   * Counts the number of overdue saving goals (target not reached and past due date) for a user.
   *
   * @param user the user entity
   * @return the number of overdue saving goals
   */
  @Query(
      "SELECT COUNT(g) FROM SavingGoal g WHERE g.target > g.contributed AND g.user = ?1 AND g.date < CURDATE()")
  int findCountOverdueSavingGoalsByUser(@Param("user") User user);

  /**
   * Counts the number of completed saving goals (target reached) for a user.
   *
   * @param user the user entity
   * @return the number of completed saving goals
   */
  @Query("SELECT COUNT(g) FROM SavingGoal g WHERE g.target <= g.contributed AND g.user = ?1")
  int findCountCompletedSavingGoalsByUser(@Param("user") User user);

  /**
   * Retrieves all pending saving goals (target not yet reached) for a user.
   *
   * @param user the user entity
   * @return a list of pending saving goals, or an empty list if none exist
   */
  @Query("SELECT g FROM SavingGoal g WHERE g.target > g.contributed AND g.user = ?1")
  List<SavingGoal> findPendingSavingGoalsForUser(@Param("user") User user);

  /**
   * Retrieves all completed saving goals (target reached) for a user.
   *
   * @param user the user entity
   * @return a list of completed saving goals, or an empty list if none exist
   */
  @Query("SELECT g FROM SavingGoal g WHERE g.target <= g.contributed AND g.user = ?1")
  List<SavingGoal> findCompletedSavingGoalsForUser(@Param("user") User user);

  /**
   * Checks if a saving goal exists by its ID and associated user.
   *
   * @param id the saving goal ID
   * @param user the user entity
   * @return true if the saving goal exists, false otherwise
   */
  boolean existsByIdAndUser(@Param("id") long id, @Param("user") User user);
}
