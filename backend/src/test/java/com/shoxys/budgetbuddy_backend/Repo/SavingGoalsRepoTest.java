package com.shoxys.budgetbuddy_backend.Repo;

import static org.assertj.core.api.Assertions.assertThat;

import com.shoxys.budgetbuddy_backend.Entities.Account;
import com.shoxys.budgetbuddy_backend.Entities.SavingGoal;
import com.shoxys.budgetbuddy_backend.Entities.User;
import com.shoxys.budgetbuddy_backend.Enums.AccountType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles("test")
@DataJpaTest
class SavingGoalsRepoTest {

  @Autowired private SavingGoalsRepo savingGoalsRepo;

  @Autowired private UserRepo userRepo;

  @Autowired private AccountRepo accountRepo;

  private User user;
  private Account account;

  @BeforeEach
  void setUp() {
    user =
        userRepo
            .getUserByEmail("test@example.com")
            .orElseGet(
                () -> {
                  User u = new User();
                  u.setEmail("test@example.com");
                  u.setHashedPassword("secret");
                  return userRepo.save(u);
                });

    account = new Account();
    account.setName("Test Account");
    account.setType(AccountType.GOALSAVINGS);
    account.setBalance(BigDecimal.valueOf(1000));
    account.setManual(true);
    accountRepo.save(account);
  }

  @AfterEach
  void tearDown() {
    savingGoalsRepo.deleteAll();
    accountRepo.deleteAll();
    userRepo.deleteAll();
  }

  @Test
  void testFindTop3ByUser_IdOrderByTargetDesc() {
    for (int i = 1; i <= 5; i++) {
      SavingGoal goal =
          new SavingGoal(
              "Goal " + i,
              BigDecimal.valueOf(i * 100),
              BigDecimal.ZERO,
              LocalDate.now(),
              null,
              account,
              user);
      savingGoalsRepo.save(goal);
    }

    List<SavingGoal> top3 = savingGoalsRepo.findTop3ByUser_IdOrderByTargetDesc(user.getId());
    assertThat(top3).hasSize(3);
    assertThat(top3.get(0).getTarget()).isEqualByComparingTo("500");
  }

  @Test
  void testFindPendingSavingGoalsForUser() {
    SavingGoal pending =
        new SavingGoal(
            "Pending Goal",
            BigDecimal.valueOf(1000),
            BigDecimal.valueOf(200),
            LocalDate.now(),
            null,
            account,
            user);
    savingGoalsRepo.save(pending);

    List<SavingGoal> result = savingGoalsRepo.findPendingSavingGoalsForUser(user);
    assertThat(result).contains(pending);
  }

  @Test
  void testFindCompletedSavingGoalsForUser() {
    SavingGoal completed =
        new SavingGoal(
            "Completed Goal",
            BigDecimal.valueOf(500),
            BigDecimal.valueOf(500),
            LocalDate.now(),
            null,
            account,
            user);
    savingGoalsRepo.save(completed);

    List<SavingGoal> result = savingGoalsRepo.findCompletedSavingGoalsForUser(user);
    assertThat(result).contains(completed);
  }

  @Test
  void testFindCountCompletedSavingGoalsByUser() {
    SavingGoal g1 =
        new SavingGoal(
            "Done",
            BigDecimal.valueOf(300),
            BigDecimal.valueOf(300),
            LocalDate.now(),
            null,
            account,
            user);
    SavingGoal g2 =
        new SavingGoal(
            "Not Done",
            BigDecimal.valueOf(400),
            BigDecimal.valueOf(100),
            LocalDate.now(),
            null,
            account,
            user);
    savingGoalsRepo.saveAll(List.of(g1, g2));

    int count = savingGoalsRepo.findCountCompletedSavingGoalsByUser(user);
    assertThat(count).isEqualTo(1);
  }

  @Test
  void testFindCountInProgressSavingGoalsByUser() {
    SavingGoal g =
        new SavingGoal(
            "In Progress",
            BigDecimal.valueOf(300),
            BigDecimal.valueOf(100),
            LocalDate.now(),
            null,
            account,
            user);
    savingGoalsRepo.save(g);

    int count = savingGoalsRepo.findCountInProgressSavingGoalsByUser(user);
    assertThat(count).isEqualTo(1);
  }

  @Test
  void testFindCountOverdueSavingGoalsByUser() {
    SavingGoal overdue =
        new SavingGoal(
            "Overdue",
            BigDecimal.valueOf(1000),
            BigDecimal.valueOf(200),
            LocalDate.now().minusDays(10),
            null,
            account,
            user);
    savingGoalsRepo.save(overdue);

    int count = savingGoalsRepo.findCountOverdueSavingGoalsByUser(user);
    assertThat(count).isEqualTo(1);
  }

  @Test
  void testAmountOfCompletedGoalsInWeek() {
    LocalDate now = LocalDate.now();
    SavingGoal g =
        new SavingGoal(
            "Weekly Done", BigDecimal.TEN, BigDecimal.TEN, now.minusDays(2), null, account, user);
    savingGoalsRepo.save(g);

    int count = savingGoalsRepo.amountOfCompletedGoalsInWeek(user, now.minusDays(3), now);
    assertThat(count).isEqualTo(1);
  }

  @Test
  void testAmountOfInProgressGoalsInWeek() {
    LocalDate now = LocalDate.now();
    SavingGoal g =
        new SavingGoal(
            "In Week",
            BigDecimal.valueOf(100),
            BigDecimal.valueOf(50),
            now.minusDays(1),
            null,
            account,
            user);
    savingGoalsRepo.save(g);

    int count = savingGoalsRepo.amountOfInProgressGoalsInWeek(user, now.minusDays(3), now);
    assertThat(count).isEqualTo(1);
  }

  @Test
  void testSumContributionsByUser() {
    SavingGoal g1 =
        new SavingGoal(
            "One",
            BigDecimal.valueOf(100),
            BigDecimal.valueOf(40),
            LocalDate.now(),
            null,
            account,
            user);
    SavingGoal g2 =
        new SavingGoal(
            "Two",
            BigDecimal.valueOf(100),
            BigDecimal.valueOf(60),
            LocalDate.now(),
            null,
            account,
            user);
    savingGoalsRepo.saveAll(List.of(g1, g2));

    BigDecimal total = savingGoalsRepo.sumContributionsByUser(user);
    assertThat(total).isEqualByComparingTo("100");
  }

  @Test
  @Transactional
  @Rollback(false)
  void testUpdateSavingGoalContribution() {
    SavingGoal goal =
        new SavingGoal(
            "Contribution",
            BigDecimal.valueOf(1000),
            BigDecimal.valueOf(200),
            LocalDate.now(),
            null,
            account,
            user);
    savingGoalsRepo.save(goal);

    savingGoalsRepo.updateSavingGoalContribution(user, goal.getId(), BigDecimal.valueOf(50));
    Optional<SavingGoal> updated = savingGoalsRepo.findById(goal.getId());

    assertThat(updated).isPresent();
    assertThat(updated.get().getContributed()).isEqualByComparingTo("250");
  }

  @Test
  void testDeleteSavingGoalByIdAndUser() {
    SavingGoal goal =
        new SavingGoal(
            "To Delete",
            BigDecimal.valueOf(100),
            BigDecimal.ZERO,
            LocalDate.now(),
            null,
            account,
            user);
    savingGoalsRepo.save(goal);

    savingGoalsRepo.deleteSavingGoalByIdAndUser(goal.getId(), user);
    Optional<SavingGoal> deleted = savingGoalsRepo.findById(goal.getId());

    assertThat(deleted).isEmpty();
  }

  @Test
  void testFindSavingGoalByIdAndUser() {
    SavingGoal goal =
        new SavingGoal(
            "Find Me",
            BigDecimal.valueOf(500),
            BigDecimal.ZERO,
            LocalDate.now(),
            null,
            account,
            user);
    savingGoalsRepo.save(goal);

    Optional<SavingGoal> result = savingGoalsRepo.findSavingGoalByIdAndUser(goal.getId(), user);
    assertThat(result).isPresent();
    assertThat(result.get().getTitle()).isEqualTo("Find Me");
  }
}
