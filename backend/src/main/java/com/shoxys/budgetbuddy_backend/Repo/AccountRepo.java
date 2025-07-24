package com.shoxys.budgetbuddy_backend.Repo;

import com.shoxys.budgetbuddy_backend.Entities.Account;
import com.shoxys.budgetbuddy_backend.Entities.User;
import com.shoxys.budgetbuddy_backend.Enums.AccountType;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

/**
 * Repository interface for managing {@link AccountRepo} entities, providing CRUD operations and
 * custom queries for retrieving and updating accounts based on user and account type.
 */
public interface AccountRepo extends CrudRepository<Account, Long> {

  /**
   * Calculates the total balance of all accounts for a given user.
   *
   * @param userId the ID of the user
   * @return the sum of all account balances, or {@code BigDecimal.ZERO} if no accounts exist
   */
  @Query("SELECT COALESCE(SUM(a.balance), 0) FROM Account a WHERE a.user.id = :userId")
  BigDecimal findTotalBalanceByUserId(@Param("userId") Long userId);

  /**
   * Retrieves all accounts for a user, including type, name, and balance.
   *
   * @param userId the ID of the user
   * @return a list of accounts, or an empty list if none are found
   */
  List<Account> findAccountsTypeNameBalanceByUserId(long userId);

  /**
   * Retrieves all accounts for a user, including name and balance.
   *
   * @param userId the ID of the user
   * @return a list of accounts, or an empty list if none are found
   */
  List<Account> findAccountsNameBalanceByUserId(long userId);

  /**
   * Retrieves all accounts associated with a user.
   *
   * @param user the user entity
   * @return a list of accounts, or an empty list if none are found
   */
  List<Account> findAccountsByUser(User user);

  /**
   * Finds an account by its ID and associated user.
   *
   * @param id the account ID
   * @param user the user entity
   * @return an {@code Optional} containing the account, or empty if not found
   */
  Optional<Account> findByIdAndUser(Long id, User user);

  /**
   * Finds an account by user and account type.
   *
   * @param user the user entity
   * @param type the account type (e.g., SPENDING, GOALSAVINGS)
   * @return an {@code Optional} containing the account, or empty if not found
   */
  Optional<Account> findAccountByUserAndType(User user, AccountType type);

  /**
   * Finds an account by user, name, and account type.
   *
   * @param user the user entity
   * @param name the account name
   * @param accountType the account type (e.g., SPENDING, GOALSAVINGS)
   * @return an {@code Optional} containing the account, or empty if not found
   */
  Optional<Account> findByUserAndNameAndType(User user, String name, AccountType accountType);
}
