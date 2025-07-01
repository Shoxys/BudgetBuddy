package com.shoxys.budgetbuddy_backend.Repo;

import com.shoxys.budgetbuddy_backend.Entities.Account;
import com.shoxys.budgetbuddy_backend.Entities.Transaction;
import com.shoxys.budgetbuddy_backend.Entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransactionRepo extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUser_Id(Long userId);

    @Query("SELECT t FROM Transaction t WHERE t.user.id = :userId AND t.date BETWEEN :startDate AND :endDate")
    List<Transaction> findByUserIdAndDateBetween(Long userId, LocalDate startDate, LocalDate endDate);

    Page<Transaction> findByUser_Id(Long userId, Pageable pageable);

    List<Transaction> findByUser_IdOrderByDateAsc(Long userId);

    List<Transaction> findByUser_IdOrderByDateDesc(Long userId);

    @Query("SELECT t FROM Transaction t WHERE t.user.id = :userId AND t.date >= :startDate")
    List<Transaction> findByUserIdAndDateAfter(Long userId, LocalDate startDate);

    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.user.id = :userId AND t.date BETWEEN :startDate AND :endDate")
    Long countByUserIdAndDateBetween(Long userId, LocalDate startDate, LocalDate endDate);

    @Query("SELECT MIN(t.date), MAX(t.date) FROM Transaction t WHERE t.user.id = :userId AND t.date BETWEEN :startDate AND :endDate")
    Object[] findDateRangeForUserTransactions(Long userId, LocalDate startDate, LocalDate endDate);

    void deleteAllByIdIn(List<Long> ids);
}
