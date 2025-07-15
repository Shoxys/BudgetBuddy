package com.shoxys.budgetbuddy_backend.Repo;

import com.shoxys.budgetbuddy_backend.DTOs.Dashboard.ExpenseAnalysis;
import com.shoxys.budgetbuddy_backend.DTOs.Dashboard.RecentTransactions;
import com.shoxys.budgetbuddy_backend.Entities.Account;
import com.shoxys.budgetbuddy_backend.Entities.Transaction;
import com.shoxys.budgetbuddy_backend.Entities.User;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepo extends JpaRepository<Transaction, Long> {
  List<Transaction> findByUser_Id(Long userId);

  @Query(
      value = "SELECT * FROM transactions WHERE user_id = ?1 AND date BETWEEN ?2 AND ?3",
      nativeQuery = true)
  List<Transaction> findByUserIdAndDateBetween(Long userId, LocalDate startDate, LocalDate endDate);

  Page<Transaction> findByUser_Id(Long userId, Pageable pageable);

  List<Transaction> findByUser_IdOrderByDateAsc(Long userId);

  List<Transaction> findByUser_IdOrderByDateDesc(Long userId);

  @Query(
      value =
          """
        SELECT SUM(t.amount)
        FROM transactions t
        JOIN accounts a ON t.account_id = a.id
        WHERE t.user_id = ?1
          AND a.type = 'SPENDING'
          AND t.amount > 0
          AND MONTH(t.date) = MONTH(CURRENT_DATE())
          AND YEAR(t.date) = YEAR(CURRENT_DATE())
        """,
      nativeQuery = true)
  BigDecimal getTotalCreditThisMonth(Long userId);

  @Query(
      value =
          """
    SELECT SUM(t.amount)
    FROM transactions t
    WHERE t.user_id = ?1
      AND t.amount > 0
      AND t.date BETWEEN ?2 AND ?3
    """,
      nativeQuery = true)
  BigDecimal getTotalCreditBetween(Long userId, LocalDate start, LocalDate end);

  @Query(
      value =
          """
        SELECT SUM(t.amount)
        FROM transactions t
        JOIN accounts a ON t.account_id = a.id
        WHERE t.user_id = ?1
          AND a.type = 'SPENDING'
          AND t.amount < 0
          AND MONTH(t.date) = MONTH(CURRENT_DATE())
          AND YEAR(t.date) = YEAR(CURRENT_DATE())
        """,
      nativeQuery = true)
  BigDecimal getTotalDebitThisMonth(Long userId);

  @Query(
      value =
          """
    SELECT SUM(t.amount)
    FROM transactions t
    WHERE t.user_id = ?1
      AND t.amount < 0
      AND t.date BETWEEN ?2 AND ?3
    """,
      nativeQuery = true)
  BigDecimal getTotalDebitBetween(Long userId, LocalDate startDate, LocalDate endDate);

  @Query(
      "SELECT SUM(t.amount), 0 "
          + "FROM Transaction t "
          + "WHERE t.amount > 0 AND t.user.id = ?1 AND YEAR(t.date) = ?2 "
          + "GROUP BY MONTH(t.date) "
          + "ORDER BY MONTH(t.date)")
  List<BigDecimal> getMonthlyIncomeForYear(Long userId, int year);

  @Query(
      value =
          """
        SELECT category, SUM(amount) as total_amount
        FROM transactions t
        WHERE t.user_id = ?1
        GROUP BY category
        ORDER BY total_amount DESC
        LIMIT 5
    """,
      nativeQuery = true)
  List<ExpenseAnalysis> findTop5CategoriesByAmount(Long userId);

  @Query(
      value =
          """
        SELECT CAST(date AS DATE) AS date, description, category, amount
        FROM transactions t
        WHERE t.user_id = ?1
        ORDER BY date DESC
        LIMIT 3
    """,
      nativeQuery = true)
  List<RecentTransactions> findLatest3TransactionSummaries(Long userId);

  Optional<Transaction> findTransactionByUserAndId(User user, long id);

  @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.account = ?1")
  BigDecimal sumAmountsByAccount(Account account);

  void deleteAllByIdInAndUser(List<Long> ids, User user);
}
