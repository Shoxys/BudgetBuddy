package com.shoxys.budgetbuddy_backend.Services;

import static com.shoxys.budgetbuddy_backend.Enums.AccountType.SPENDING;

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
import com.shoxys.budgetbuddy_backend.Utils;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class AccountService {
  private final AccountRepo accountRepo;
  private final UserRepo userRepo;
  private final TransactionRepo transactionRepo;
  private final SavingGoalsRepo savingGoalsRepo;

  public AccountService(
      AccountRepo accountRepo,
      UserRepo userRepo,
      TransactionRepo transactionRepo,
      SavingGoalsRepo savingGoalsRepo) {
    this.accountRepo = accountRepo;
    this.userRepo = userRepo;
    this.transactionRepo = transactionRepo;
    this.savingGoalsRepo = savingGoalsRepo;
  }

  public List<Account> getAccountsByUserId(long userId) {
    User user =
        userRepo.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
    return accountRepo.findAccountsByUser(user);
  }

  @Transactional
  public Account upsertAccountBalance(
      String email, String name, AccountType type, BigDecimal newBalance) {
    User user =
        userRepo.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User not found"));

    if (Utils.nullOrEmpty(name)) {
      throw new IllegalArgumentException("Name cannot be empty");
    }

    if (type == null) {
      throw new IllegalArgumentException("Type cannot be empty");
    }

    if (newBalance == null) {
      throw new IllegalArgumentException("New balance cannot be null");
    }

    Optional<Account> optionalAccount = accountRepo.findAccountByUserAndType(user, type);

    if (optionalAccount.isPresent()) {
      Account updatedAccount = optionalAccount.get();
      updatedAccount.setBalance(newBalance);
      accountRepo.save(updatedAccount);
      return updatedAccount;
    } else {
      Account newAccount = new Account(name, type, null, newBalance, true, user);
      newAccount.setBalance(newBalance);
      return newAccount;
    }
  }

  public Account createSpendingAccount(User user, BigDecimal balance) {
    Account newAccount =
        new Account("Spending Account", AccountType.SPENDING, null, balance, true, user);
    accountRepo.save(newAccount);
    return newAccount;
  }

  public Account createGoalSavingsAccount(User user, BigDecimal balance) {
    Account newAccount =
        new Account("Goal Savings", AccountType.GOALSAVINGS, null, balance, true, user);
    accountRepo.save(newAccount);
    return newAccount;
  }

  public Account syncSpendingAccountBalance(Transaction transaction, BigDecimal newAmount) {
    // Spending account ties to transaction
    Account account = transaction.getAccount();
    // Old amount stored
    BigDecimal oldAmount = transaction.getAmount();

    // Calculate delta and apply to account balance
    BigDecimal delta = newAmount.subtract(oldAmount);
    account.setBalance(account.getBalance().add(delta));
    return accountRepo.save(account);
  }

  public void updateAccountBalance(Account account, BigDecimal newBalance) {
    account.setBalance(newBalance);
    accountRepo.save(account);
  }

  public void recalculateBalanceForSpendingAccount(Account account) {
    BigDecimal newBalance = transactionRepo.sumAmountsByAccount(account);
    account.setBalance(newBalance != null ? newBalance : BigDecimal.ZERO);
    accountRepo.save(account);
  }

  public void recalculateGoalSavingsBalance(User user) {
    Account goalSavingsAccount =
        accountRepo
            .findAccountByUserAndType(user, AccountType.GOALSAVINGS)
            .orElseThrow(() -> new AccountNotFoundException("Goal Savings account not found"));

    BigDecimal totalContributed = savingGoalsRepo.sumContributionsByUser(user);
    goalSavingsAccount.setBalance(totalContributed != null ? totalContributed : BigDecimal.ZERO);
    accountRepo.save(goalSavingsAccount);
  }

  public Account handleAccountNoAssign(int accountNo, User user) {
    // Handle AccountNo assignment & account creation if not already exists
    return accountRepo
        .findByAccountNo(accountNo)
        .orElseGet(
            () -> {
              Account newAccount = new Account();
              newAccount.setAccountNo(accountNo);
              newAccount.setType(SPENDING);
              newAccount.setName("Spending Account");
              newAccount.setUser(user);
              return accountRepo.save(newAccount);
            });
  }
}
