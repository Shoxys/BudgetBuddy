package com.shoxys.budgetbuddy_backend.Services;

import com.shoxys.budgetbuddy_backend.Entities.Account;
import com.shoxys.budgetbuddy_backend.Entities.User;
import com.shoxys.budgetbuddy_backend.Enums.AccountType;
import com.shoxys.budgetbuddy_backend.Repo.AccountRepo;
import com.shoxys.budgetbuddy_backend.Repo.UserRepo;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountNotFoundException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class AccountService {
    private final AccountRepo accountRepo;
    private final UserRepo userRepo;

    public AccountService(AccountRepo accountRepo, UserRepo userRepo) {
        this.accountRepo = accountRepo;
        this.userRepo = userRepo;
    }

    public List<Account> getAccountsByUserId(long userId) {
        return accountRepo.findAccountsByUser_Id(userId);
    }

    public void upsertAccount(String email, String name, AccountType type, BigDecimal newBalance) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

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
}
