package com.shoxys.budgetbuddy_backend.Services;

import com.shoxys.budgetbuddy_backend.Entities.Account;
import com.shoxys.budgetbuddy_backend.Entities.Transaction;
import com.shoxys.budgetbuddy_backend.Entities.User;
import com.shoxys.budgetbuddy_backend.Enums.AccountType;
import com.shoxys.budgetbuddy_backend.Repo.AccountRepo;
import com.shoxys.budgetbuddy_backend.Repo.SavingGoalsRepo;
import com.shoxys.budgetbuddy_backend.Repo.TransactionRepo;
import com.shoxys.budgetbuddy_backend.Repo.UserRepo;
import com.shoxys.budgetbuddy_backend.Utils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class AccountService {
    @Autowired
    private final AccountRepo accountRepo;
    @Autowired
    private final UserRepo userRepo;
    @Autowired
    TransactionRepo transactionRepo;
    @Autowired
    SavingGoalsRepo  savingGoalsRepo;

    public AccountService(AccountRepo accountRepo, UserRepo userRepo) {
        this.accountRepo = accountRepo;
        this.userRepo = userRepo;
    }

    public List<Account> getAccountsByUserId(long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return accountRepo.findAccountsByUser(user);
    }

    @Transactional
    public void upsertAccount(String email, String name, AccountType type, BigDecimal newBalance) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (Utils.nullOrEmpty(name)){
            throw new IllegalArgumentException("Name cannot be empty");
        }

        if (type == null) {
            throw new IllegalArgumentException("Type cannot be empty");
        }

        if (Utils.nullOrNegative(newBalance)){
            throw new IllegalArgumentException("New balance cannot be negative");
        }

        Optional<Account> optionalAccount = accountRepo.findAccountByUserAndType(user, type);

        if(optionalAccount.isPresent()) {
            Account account = optionalAccount.get();
            account.setBalance(newBalance);
            accountRepo.save(account);
        } else {
            Account newAccount = new Account(name, type, null, newBalance, true, user );
            newAccount.setBalance(newBalance);
        }
    }

    public Account createSpendingAccount(User user, BigDecimal balance) {
        Account newAccount = new Account("Spending Account", AccountType.SPENDING, null, balance, true, user);
        accountRepo.save(newAccount);
        return newAccount;
    }

    public Account createGoalSavingsAccount(User user, BigDecimal balance) {
        Account newAccount = new Account("Goal Savings", AccountType.GOALSAVINGS, null, balance, true, user);
        accountRepo.save(newAccount);
        return newAccount;
    }

    public Account syncSpendingAccountBalance(Transaction transaction, BigDecimal  newAmount) {
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
        Account goalSavingsAccount = accountRepo.findAccountByUserAndType(user, AccountType.GOALSAVINGS)
                .orElseThrow(() -> new EntityNotFoundException("Goal Savings account not found"));

        BigDecimal totalContributed = savingGoalsRepo.sumContributionsByUser(user);
        goalSavingsAccount.setBalance(totalContributed != null ? totalContributed : BigDecimal.ZERO);
        accountRepo.save(goalSavingsAccount);
    }

}
