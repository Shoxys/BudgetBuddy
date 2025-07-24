package com.shoxys.budgetbuddy_backend.Services;

import com.shoxys.budgetbuddy_backend.Entities.Account;
import com.shoxys.budgetbuddy_backend.Entities.Transaction;
import com.shoxys.budgetbuddy_backend.Entities.User;
import com.shoxys.budgetbuddy_backend.Enums.AccountType;
import com.shoxys.budgetbuddy_backend.Exceptions.AccountNotFoundException;
import com.shoxys.budgetbuddy_backend.Exceptions.UserNotFoundException;
import com.shoxys.budgetbuddy_backend.Repo.AccountRepo;
import com.shoxys.budgetbuddy_backend.Repo.SavingGoalsRepo;
import com.shoxys.budgetbuddy_backend.Repo.TransactionRepo;
import com.shoxys.budgetbuddy_backend.Repo.UserRepo;
import com.shoxys.budgetbuddy_backend.Utils.Utils;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/** Service for managing user accounts, including creation, balance updates, and retrieval. */
@Service
public class AccountService {
  private static final Logger logger = LoggerFactory.getLogger(AccountService.class);
  private final AccountRepo accountRepo;
  private final UserRepo userRepo;
  private final TransactionRepo transactionRepo;
  private final SavingGoalsRepo savingGoalsRepo;

  /**
   * Constructs an AccountService with required dependencies.
   *
   * @param accountRepo Repository for account-related data access
   * @param userRepo Repository for user-related data access
   * @param transactionRepo Repository for transaction-related data access
   * @param savingGoalsRepo Repository for saving goals data access
   */
  public AccountService(
      AccountRepo accountRepo,
      UserRepo userRepo,
      TransactionRepo transactionRepo,
      SavingGoalsRepo savingGoalsRepo) {
    this.accountRepo = accountRepo;
    this.userRepo = userRepo;
    this.transactionRepo = transactionRepo;
    this.savingGoalsRepo = savingGoalsRepo;
    logger.info("Initializing AccountService");
  }

  /**
   * Retrieves all accounts for a given user.
   *
   * @param userId the ID of the user
   * @return a list of accounts associated with the user
   * @throws IllegalArgumentException if userId is not positive
   * @throws UserNotFoundException if the user is not found
   */
  public List<Account> getAccountsByUserId(Long userId) {
    logger.debug("Fetching accounts for user ID: {}", userId);
    Utils.validatePositiveId(userId, "User ID must be positive");
    User user =
        userRepo
            .findById(userId)
            .orElseThrow(
                () -> {
                  logger.error("User not found: {}", userId);
                  return new UserNotFoundException("User not found: " + userId);
                });
    List<Account> accounts = accountRepo.findAccountsByUser(user);
    logger.info(
        "Retrieved {} accounts for user ID: {}", accounts == null ? 0 : accounts.size(), userId);
    return accounts == null ? List.of() : accounts;
  }

  /**
   * Creates or updates an account's balance for a user identified by email.
   *
   * @param email the email of the user
   * @param id the account ID (optional for updates)
   * @param name the account name
   * @param type the account type
   * @param newBalance the new balance for the account
   * @return the updated or created account
   * @throws IllegalArgumentException if email, name, type, or balance is invalid
   * @throws UserNotFoundException if the user is not found
   */
  @Transactional
  public Account upsertAccountBalance(
      String email, Long id, String name, AccountType type, BigDecimal newBalance) {
    logger.debug(
        "Upserting account balance for user: {}, ID: {}, name: {}, type: {}",
        email,
        id,
        name,
        type);
    if (Utils.nullOrEmpty(email)) {
      logger.error("Email is null or empty");
      throw new IllegalArgumentException("Email cannot be null or empty");
    }
    if (!Utils.isEmail(email)) {
      logger.error("Invalid email format: {}", email);
      throw new IllegalArgumentException("Invalid email format");
    }
    if (Utils.nullOrEmpty(name)) {
      logger.error("Account name is null or empty");
      throw new IllegalArgumentException("Name cannot be null or empty");
    }
    if (type == null) {
      logger.error("Account type is null");
      throw new IllegalArgumentException("Type cannot be null");
    }
    if (Utils.nullOrNegative(newBalance)) {
      logger.error("Balance is null or negative: {}", newBalance);
      throw new IllegalArgumentException("Balance cannot be null or negative");
    }

    User user =
        userRepo
            .findByEmail(email)
            .orElseThrow(
                () -> {
                  logger.error("User not found: {}", email);
                  return new UserNotFoundException("User not found: " + email);
                });

    Optional<Account> optionalAccount =
        (id != null)
            ? accountRepo.findByIdAndUser(id, user)
            : accountRepo.findByUserAndNameAndType(user, name, type);

    Account account =
        optionalAccount.orElseGet(
            () -> {
              logger.debug("Creating new account for user: {}", email);
              return new Account(name, type, null, newBalance, true, user);
            });

    account.setBalance(newBalance);
    account.setName(name);
    Account savedAccount = accountRepo.save(account);
    logger.info("Upserted account ID: {} for user: {}", savedAccount.getId(), email);
    return savedAccount;
  }

  /**
   * Creates a spending account for a user with the specified balance.
   *
   * @param user the user to associate with the account
   * @param balance the initial balance
   * @return the created spending account
   * @throws IllegalArgumentException if user is null or balance is invalid
   */
  @Transactional
  public Account createSpendingAccount(User user, BigDecimal balance) {
    logger.debug("Creating spending account for user ID: {}", user != null ? user.getId() : null);
    if (user == null) {
      logger.error("User is null");
      throw new IllegalArgumentException("User cannot be null");
    }
    Utils.validatePositiveId(user.getId(), "User ID must be positive");
    if (Utils.nullOrNegative(balance)) {
      logger.error("Invalid balance for spending account: {}", balance);
      throw new IllegalArgumentException("Balance cannot be null or negative");
    }
    Account newAccount =
        new Account("Spending Account", AccountType.SPENDING, null, balance, true, user);
    Account savedAccount = accountRepo.save(newAccount);
    logger.info(
        "Created spending account ID: {} for user ID: {}", savedAccount.getId(), user.getId());
    return savedAccount;
  }

  /**
   * Creates a goal savings account for a user with the specified balance.
   *
   * @param user the user to associate with the account
   * @param balance the initial balance
   * @return the created goal savings account
   * @throws IllegalArgumentException if user is null or balance is invalid
   */
  @Transactional
  public Account createGoalSavingsAccount(User user, BigDecimal balance) {
    logger.debug(
        "Creating goal savings account for user ID: {}", user != null ? user.getId() : null);
    if (user == null) {
      logger.error("User is null");
      throw new IllegalArgumentException("User cannot be null");
    }
    Utils.validatePositiveId(user.getId(), "User ID must be positive");
    if (Utils.nullOrNegative(balance)) {
      logger.error("Invalid balance for goal savings account: {}", balance);
      throw new IllegalArgumentException("Balance cannot be null or negative");
    }
    Account newAccount =
        new Account("Goal Savings", AccountType.GOALSAVINGS, null, balance, true, user);
    Account savedAccount = accountRepo.save(newAccount);
    logger.info(
        "Created goal savings account ID: {} for user ID: {}", savedAccount.getId(), user.getId());
    return savedAccount;
  }

  /**
   * Synchronizes an account's balance based on a transaction's new amount.
   *
   * @param transaction the transaction to sync
   * @param newAmount the new transaction amount
   * @return the updated account
   * @throws IllegalArgumentException if transaction or new amount is invalid
   * @throws AccountNotFoundException if no account is associated with the transaction
   */
  @Transactional
  public Account syncSpendingAccountBalance(Transaction transaction, BigDecimal newAmount) {
    logger.debug(
        "Syncing spending account balance for transaction ID: {}",
        transaction != null ? transaction.getId() : null);
    if (transaction == null) {
      logger.error("Transaction is null");
      throw new IllegalArgumentException("Transaction cannot be null");
    }
    Utils.validatePositiveId(transaction.getId(), "Transaction ID must be positive");
    Account account = transaction.getAccount();
    if (account == null) {
      logger.error("No account associated with transaction ID: {}", transaction.getId());
      throw new AccountNotFoundException("No account associated with transaction");
    }
    if (Utils.nullOrNegative(newAmount)) {
      logger.error("Invalid new amount: {}", newAmount);
      throw new IllegalArgumentException("New amount cannot be null or negative");
    }
    BigDecimal oldAmount = Optional.ofNullable(transaction.getAmount()).orElse(BigDecimal.ZERO);
    BigDecimal delta = newAmount.subtract(oldAmount);
    account.setBalance(account.getBalance().add(delta));
    Account savedAccount = accountRepo.save(account);
    logger.info(
        "Synced account ID: {} balance to: {}", savedAccount.getId(), savedAccount.getBalance());
    return savedAccount;
  }

  /**
   * Updates an account's balance to a new value.
   *
   * @param account the account to update
   * @param newBalance the new balance
   * @throws IllegalArgumentException if account or balance is invalid
   */
  @Transactional
  public void updateAccountBalance(Account account, BigDecimal newBalance) {
    logger.debug(
        "Updating account balance for account ID: {}", account != null ? account.getId() : null);
    if (account == null) {
      logger.error("Account is null");
      throw new IllegalArgumentException("Account cannot be null");
    }
    Utils.validatePositiveId(account.getId(), "Account ID must be positive");
    if (Utils.nullOrNegative(newBalance)) {
      logger.error("Invalid balance: {}", newBalance);
      throw new IllegalArgumentException("Balance cannot be null or negative");
    }
    account.setBalance(newBalance);
    accountRepo.save(account);
    logger.info("Updated account ID: {} balance to: {}", account.getId(), newBalance);
  }

  /**
   * Retrieves the balance of an account by user email, name, and type.
   *
   * @param email the email of the user
   * @param name the account name
   * @param type the account type
   * @return the account balance, or null if the account is not found
   * @throws IllegalArgumentException if email, name, or type is invalid
   * @throws UserNotFoundException if the user is not found
   */
  @Transactional
  public BigDecimal getAccountBalance(String email, String name, AccountType type) {
    logger.debug("Fetching account balance for user: {}, name: {}, type: {}", email, name, type);
    if (Utils.nullOrEmpty(email)) {
      logger.error("Email is null or empty");
      throw new IllegalArgumentException("Email cannot be null or empty");
    }
    if (!Utils.isEmail(email)) {
      logger.error("Invalid email format: {}", email);
      throw new IllegalArgumentException("Invalid email format");
    }
    if (Utils.nullOrEmpty(name)) {
      logger.error("Account name is null or empty");
      throw new IllegalArgumentException("Name cannot be null or empty");
    }
    if (type == null) {
      logger.error("Account type is null");
      throw new IllegalArgumentException("Type cannot be null");
    }
    User user =
        userRepo
            .findByEmail(email)
            .orElseThrow(
                () -> {
                  logger.error("User not found: {}", email);
                  return new UserNotFoundException("User not found: " + email);
                });
    BigDecimal balance =
        accountRepo
            .findByUserAndNameAndType(user, name, type)
            .map(Account::getBalance)
            .orElse(null);
    logger.info(
        "Retrieved balance: {} for user: {}, name: {}, type: {}", balance, email, name, type);
    return balance;
  }

  /**
   * Recalculates the balance of a spending account based on the latest transaction.
   *
   * @param account the account to recalculate
   * @throws IllegalArgumentException if account is invalid
   */
  @Transactional
  public void recalculateBalanceForSpendingAccount(Account account) {
    logger.debug(
        "Recalculating balance for spending account ID: {}",
        account != null ? account.getId() : null);
    if (account == null) {
      logger.error("Account is null");
      throw new IllegalArgumentException("Account cannot be null");
    }
    Utils.validatePositiveId(account.getId(), "Account ID must be positive");
    transactionRepo.findByAccountOrderByDateDesc(account).stream()
        .findFirst()
        .ifPresent(
            transaction -> {
              BigDecimal balance = transaction.getBalanceAtTransaction();
              if (Utils.nullOrNegative(balance)) {
                logger.error(
                    "Invalid balanceAtTransaction: {} for transaction ID: {}",
                    balance,
                    transaction.getId());
                throw new IllegalArgumentException(
                    "Transaction balance cannot be null or negative");
              }
              account.setBalance(balance);
              accountRepo.save(account);
              logger.info(
                  "Recalculated balance to: {} for account ID: {}", balance, account.getId());
            });
    logger.debug("No balance update performed for account ID: {}", account.getId());
  }

  /**
   * Recalculates the balance of a goal savings account based on total contributions.
   *
   * @param user the user whose goal savings account to recalculate
   * @throws IllegalArgumentException if user is invalid
   * @throws AccountNotFoundException if no goal savings account is found
   */
  @Transactional
  public void recalculateGoalSavingsBalance(User user) {
    logger.debug(
        "Recalculating goal savings balance for user ID: {}", user != null ? user.getId() : null);
    if (user == null) {
      logger.error("User is null");
      throw new IllegalArgumentException("User cannot be null");
    }
    Utils.validatePositiveId(user.getId(), "User ID must be positive");
    Account goalSavingsAccount =
        accountRepo
            .findAccountByUserAndType(user, AccountType.GOALSAVINGS)
            .orElseThrow(
                () -> {
                  logger.error("Goal Savings account not found for user ID: {}", user.getId());
                  return new AccountNotFoundException("Goal Savings account not found");
                });
    BigDecimal totalContributed =
        Optional.ofNullable(savingGoalsRepo.sumContributionsByUser(user)).orElse(BigDecimal.ZERO);
    if (Utils.nullOrNegative(totalContributed)) {
      logger.warn("Invalid goal savings balance: {}", totalContributed);
      throw new IllegalArgumentException("Goal savings balance cannot be negative");
    }
    goalSavingsAccount.setBalance(totalContributed);
    accountRepo.save(goalSavingsAccount);
    logger.info(
        "Recalculated goal savings balance to: {} for user ID: {}", totalContributed, user.getId());
  }

  /**
   * Fetches or creates a spending account for a user.
   *
   * @param user the user to fetch or create the account for
   * @return the existing or newly created spending account
   * @throws IllegalArgumentException if user is invalid
   */
  @Transactional
  public Account handleFetchAccount(User user) {
    logger.debug(
        "Fetching or creating spending account for user ID: {}",
        user != null ? user.getId() : null);
    if (user == null) {
      logger.error("User is null");
      throw new IllegalArgumentException("User cannot be null");
    }
    Utils.validatePositiveId(user.getId(), "User ID must be positive");
    Account account =
        accountRepo
            .findAccountByUserAndType(user, AccountType.SPENDING)
            .orElseGet(
                () -> {
                  logger.debug(
                      "No spending account found, creating new for user ID: {}", user.getId());
                  return createSpendingAccount(user, BigDecimal.ZERO);
                });
    logger.info(
        "Fetched or created spending account ID: {} for user ID: {}",
        account.getId(),
        user.getId());
    return account;
  }
}
