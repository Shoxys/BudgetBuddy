package com.shoxys.budgetbuddy_backend.Repo;

import static org.assertj.core.api.Assertions.assertThat;

import com.shoxys.budgetbuddy_backend.Entities.Account;
import com.shoxys.budgetbuddy_backend.Entities.User;
import com.shoxys.budgetbuddy_backend.Enums.AccountType;
import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@DataJpaTest
public class AccountRepoTest {

  @Autowired private AccountRepo accountRepo;

  @Autowired private UserRepo userRepo;

  @Test
  @DisplayName("Should return total balance for user")
  void testFindTotalBalanceByUserId() {
    // Arrange: create a user
    User user = new User();
    user.setEmail("test@example.com");
    user.setHashedPassword("password");
    userRepo.save(user);

    // Create some accounts for the user
    Account acc1 = new Account();
    acc1.setUser(user);
    acc1.setName("Saving Account");
    acc1.setBalance(new BigDecimal("100.00"));
    acc1.setType(AccountType.SAVINGS);

    Account acc2 = new Account();
    acc2.setUser(user);
    acc2.setName("Spending Account");
    acc2.setBalance(new BigDecimal("200.00"));
    acc2.setType(AccountType.SPENDING);

    accountRepo.save(acc1);
    accountRepo.save(acc2);

    // Act
    BigDecimal total = accountRepo.findTotalBalanceByUserId(user.getId());

    // Assert
    assertThat(total).isEqualByComparingTo(new BigDecimal("300.00"));
  }

  @Test
  @DisplayName("Should return zero if user has no accounts")
  void testFindTotalBalanceByUserIdWhenNoAccounts() {
    // Arrange
    User user = new User();
    user.setEmail("noacc@example.com");
    user.setHashedPassword("password");
    userRepo.save(user);

    // Act
    BigDecimal total = accountRepo.findTotalBalanceByUserId(user.getId());

    // Assert
    assertThat(total).isEqualByComparingTo(BigDecimal.ZERO);
  }
}
