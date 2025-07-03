package com.shoxys.budgetbuddy_backend.Repo;

import com.shoxys.budgetbuddy_backend.Entities.Account;
import com.shoxys.budgetbuddy_backend.Entities.User;
import com.shoxys.budgetbuddy_backend.Enums.AccountType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface AccountRepo extends CrudRepository<Account, Long> {
    Optional<Account> findByAccountNo(int accountNo);

    List<Account> findAccountsByUser_Id(long userId);

    @Query("SELECT COALESCE(SUM(a.balance), 0) FROM Account a WHERE a.user.id = :userId")
    BigDecimal findTotalBalanceByUserId(@Param("userId") Long userId);

    List<Account> findAccountsTypeNameBalanceByUser_Id(long userId);

    List<Account> findAccountsNameBalanceByUser_Id(long userId);

    List<Account> user(User user);

    Optional<Account> findAccountsByUser_IdAndType(long userId, AccountType type);

    Optional<Account> findAccountByUserAndType(User user, AccountType type);
}
