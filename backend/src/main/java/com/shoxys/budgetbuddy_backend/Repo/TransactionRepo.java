package com.shoxys.budgetbuddy_backend.Repo;

import com.shoxys.budgetbuddy_backend.DTOs.Dashboard.ExpenseAnalysis;
import com.shoxys.budgetbuddy_backend.DTOs.Dashboard.RecentTransactions;
import com.shoxys.budgetbuddy_backend.Entities.Account;
import com.shoxys.budgetbuddy_backend.Entities.Transaction;
import com.shoxys.budgetbuddy_backend.Entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing {@link Transaction} entities, providing CRUD operations and custom queries
 * for retrieving transactions, summaries, and expense analyses by user, account, and date ranges.
 */
@Repository
public interface TransactionRepo extends JpaRepository<Transaction, Long> {

    /**
     * Retrieves all transactions for a user.
     *
     * @param userId the ID of the user
     * @return a list of transactions, or an empty list if none exist
     */
    List<Transaction> findByUser_Id(@Param("userId") Long userId);

    /**
     * Retrieves transactions for a user within a date range using a native query.
     *
     * @param userId    the ID of the user
     * @param startDate the start date of the range
     * @param endDate   the end date of the range
     * @return a list of transactions, or an empty list if none exist
     */
    @Query(value = "SELECT * FROM transactions WHERE user_id = ?1 AND date BETWEEN ?2 AND ?3", nativeQuery = true)
    List<Transaction> findByUserIdAndDateBetween(@Param("userId") Long userId,
                                                 @Param("startDate") LocalDate startDate,
                                                 @Param("endDate") LocalDate endDate);

    /**
     * Retrieves paginated transactions for a user, ordered by date ascending.
     *
     * @param userId   the ID of the user
     * @param pageable pagination information
     * @return a page of transactions
     */
    Page<Transaction> findByUser_IdOrderByDateAsc(@Param("userId") Long userId, @Param("pageable") Pageable pageable);

    /**
     * Retrieves paginated transactions for a user, ordered by date descending.
     *
     * @param userId   the ID of the user
     * @param pageable pagination information
     * @return a page of transactions
     */
    Page<Transaction> findByUser_IdOrderByDateDesc(@Param("userId") Long userId, @Param("pageable") Pageable pageable);

    /**
     * Retrieves all transactions for a user, ordered by date ascending.
     *
     * @param userId the ID of the user
     * @return a list of transactions, or an empty list if none exist
     */
    List<Transaction> findByUser_IdOrderByDateAsc(@Param("userId") Long userId);

    /**
     * Retrieves all transactions for a user, ordered by date descending.
     *
     * @param userId the ID of the user
     * @return a list of transactions, or an empty list if none exist
     */
    List<Transaction> findByUser_IdOrderByDateDesc(@Param("userId") Long userId);

    /**
     * Retrieves all transactions for an account, ordered by date descending.
     *
     * @param account the account entity
     * @return a list of transactions, or an empty list if none exist
     */
    List<Transaction> findByAccountOrderByDateDesc(@Param("account") Account account);

    /**
     * Calculates the total credit amount for a user's spending account transactions this month.
     *
     * @param userId the ID of the user
     * @return the sum of credit amounts, or null if none exist
     */
    @Query(value = """
        SELECT SUM(t.amount)
        FROM transactions t
        JOIN accounts a ON t.account_id = a.id
        WHERE t.user_id = ?1
          AND a.type = 'SPENDING'
          AND t.amount > 0
          AND MONTH(t.date) = MONTH(CURRENT_DATE())
          AND YEAR(t.date) = YEAR(CURRENT_DATE())
        """, nativeQuery = true)
    BigDecimal getTotalCreditThisMonth(@Param("userId") Long userId);

    /**
     * Calculates the total credit amount for a user within a date range.
     *
     * @param userId the ID of the user
     * @param start  the start date of the range
     * @param end    the end date of the range
     * @return the sum of credit amounts, or null if none exist
     */
    @Query(value = """
        SELECT SUM(t.amount)
        FROM transactions t
        WHERE t.user_id = ?1
          AND t.amount > 0
          AND t.date BETWEEN ?2 AND ?3
        """, nativeQuery = true)
    BigDecimal getTotalCreditBetween(@Param("userId") Long userId,
                                     @Param("start") LocalDate start,
                                     @Param("end") LocalDate end);

    /**
     * Calculates the total debit amount for a user's spending account transactions this month.
     *
     * @param userId the ID of the user
     * @return the sum of debit amounts, or null if none exist
     */
    @Query(value = """
        SELECT SUM(t.amount)
        FROM transactions t
        JOIN accounts a ON t.account_id = a.id
        WHERE t.user_id = ?1
          AND a.type = 'SPENDING'
          AND t.amount < 0
          AND MONTH(t.date) = MONTH(CURRENT_DATE())
          AND YEAR(t.date) = YEAR(CURRENT_DATE())
        """, nativeQuery = true)
    BigDecimal getTotalDebitThisMonth(@Param("userId") Long userId);

    /**
     * Calculates the total debit amount for a user within a date range.
     *
     * @param userId    the ID of the user
     * @param startDate the start date of the range
     * @param endDate   the end date of the range
     * @return the sum of debit amounts, or null if none exist
     */
    @Query(value = """
        SELECT SUM(t.amount)
        FROM transactions t
        WHERE t.user_id = ?1
          AND t.amount < 0
          AND t.date BETWEEN ?2 AND ?3
        """, nativeQuery = true)
    BigDecimal getTotalDebitBetween(@Param("userId") Long userId,
                                    @Param("startDate") LocalDate startDate,
                                    @Param("endDate") LocalDate endDate);

    /**
     * Retrieves monthly income totals for a user in a given year.
     *
     * @param userId the ID of the user
     * @param year   the year for which to retrieve income
     * @return a list of objects containing month and total income
     */
    @Query("SELECT MONTH(t.date) as month, SUM(t.amount) as income " +
            "FROM Transaction t " +
            "WHERE t.amount > 0 AND t.user.id = ?1 AND YEAR(t.date) = ?2 " +
            "GROUP BY MONTH(t.date) " +
            "ORDER BY MONTH(t.date)")
    List<Object[]> getMonthlyIncomeForYear(@Param("userId") Long userId, @Param("year") int year);

    /**
     * Retrieves the top 5 expense categories by total amount for a user, excluding transfers.
     *
     * @param userId the ID of the user
     * @return a list of up to 5 expense analysis DTOs
     */
    @Query(value = """
        SELECT category, SUM(amount) AS total_amount
        FROM transactions t
        WHERE t.user_id = ?1
          AND amount < 0
          AND LOWER(category) NOT LIKE '%transfer%'
        GROUP BY category
        ORDER BY total_amount ASC
        LIMIT 5
        """, nativeQuery = true)
    List<ExpenseAnalysis> findTop5ExpenseCategoriesByAmount(@Param("userId") Long userId);

    /**
     * Retrieves the latest 3 transaction summaries for a user.
     *
     * @param userId the ID of the user
     * @return a list of up to 3 recent transaction DTOs
     */
    @Query(value = """
        SELECT CAST(date AS DATE) AS date, description, category, amount
        FROM transactions t
        WHERE t.user_id = ?1
        ORDER BY date DESC
        LIMIT 3
        """, nativeQuery = true)
    List<RecentTransactions> findLatest3TransactionSummaries(@Param("userId") Long userId);

    /**
     * Finds a transaction by its ID and associated user.
     *
     * @param user the user entity
     * @param id   the transaction ID
     * @return an {@code Optional} containing the transaction, or empty if not found
     */
    Optional<Transaction> findTransactionByUserAndId(@Param("user") User user, @Param("id") long id);

    /**
     * Calculates the total amount of transactions for an account.
     *
     * @param account the account entity
     * @return the sum of transaction amounts, or null if none exist
     */
    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.account = ?1")
    BigDecimal sumAmountsByAccount(@Param("account") Account account);

    /**
     * Deletes multiple transactions by their IDs and associated user.
     *
     * @param ids  the list of transaction IDs
     * @param user the user entity
     */
    void deleteAllByIdInAndUser(@Param("ids") List<Long> ids, @Param("user") User user);
}