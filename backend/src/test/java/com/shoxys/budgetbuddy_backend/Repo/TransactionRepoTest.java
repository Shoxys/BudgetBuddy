package com.shoxys.budgetbuddy_backend.Repo;

import static org.assertj.core.api.Assertions.assertThat;

import com.shoxys.budgetbuddy_backend.DTOs.Dashboard.ExpenseAnalysis;
import com.shoxys.budgetbuddy_backend.DTOs.Dashboard.RecentTransactions;
import com.shoxys.budgetbuddy_backend.Entities.Account;
import com.shoxys.budgetbuddy_backend.Entities.Transaction;
import com.shoxys.budgetbuddy_backend.Entities.User;
import com.shoxys.budgetbuddy_backend.Enums.AccountType;
import com.shoxys.budgetbuddy_backend.Enums.SourceType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@DataJpaTest
public class TransactionRepoTest {
  @Autowired private TransactionRepo transactionRepo;

  @Autowired private UserRepo userRepo;

  @Autowired private AccountRepo accountRepo;

  private User user;
  private Account account;

  private static final Map<String, Transaction> transactionMap = new HashMap<>();

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
    account.setType(AccountType.SPENDING);
    account.setBalance(BigDecimal.valueOf(1000));
    account.setManual(true);
    accountRepo.save(account);
  }

  @AfterEach
  void tearDown() {
    transactionRepo.deleteAll();
    accountRepo.deleteAll();
    userRepo.deleteAll();
  }

  @Test
  void testFindByUserIdAndDateBetween() {
    final int TOTAL_TRANSACTIONS = 5;
    List<Integer> expectedDays = List.of(3, 4, 5);

    LocalDate startDate = LocalDate.of(2025, 4, expectedDays.getFirst());
    LocalDate endDate = LocalDate.of(2025, 4, expectedDays.getLast());

    for (int i = 0; i < TOTAL_TRANSACTIONS; i++) {
      Transaction transaction =
          new Transaction(
              LocalDate.of(2025, 4, 1 + i),
              BigDecimal.valueOf(100),
              "W4242 15/04",
              "Groceries",
              "Woolworths Market",
              BigDecimal.valueOf(1000),
              SourceType.MANUAL,
              account,
              user);
      transactionRepo.save(transaction);
    }

    List<Transaction> dateRangedTxns =
        transactionRepo.findByUserIdAndDateBetween(user.getId(), startDate, endDate);

    assertThat(dateRangedTxns).hasSize(expectedDays.size());

    List<Integer> actualDays =
        dateRangedTxns.stream().map(txn -> txn.getDate().getDayOfMonth()).sorted().toList();

    assertThat(actualDays).isEqualTo(expectedDays);
  }

  @Test
  void testGetTotalCreditThisMonth() {
    int thisMonth = LocalDate.now().getMonthValue();
    int lastMonth = LocalDate.now().minusMonths(1).getMonthValue();

    // Debit transactions for this month
    for (int i = 0; i < 4; i++) {
      Transaction transaction =
          new Transaction(
              LocalDate.of(2025, thisMonth, 1),
              BigDecimal.valueOf(-100),
              "W4242 15/04",
              "Groceries",
              "Woolworths Market",
              BigDecimal.valueOf(1000),
              SourceType.MANUAL,
              account,
              user);
      transactionRepo.save(transaction);
    }

    // Credit transactions for this month
    for (int i = 0; i < 4; i++) {
      Transaction transaction =
          new Transaction(
              LocalDate.of(2025, thisMonth, 1),
              BigDecimal.valueOf(100),
              "W4242 15/04",
              "Groceries",
              "Woolworths Market",
              BigDecimal.valueOf(1000),
              SourceType.MANUAL,
              account,
              user);
      transactionRepo.save(transaction);
    }
    // Credit transactions for last month
    for (int i = 0; i < 5; i++) {
      Transaction transaction =
          new Transaction(
              LocalDate.of(2025, lastMonth, 1),
              BigDecimal.valueOf(100),
              "W4242 15/04",
              "Groceries",
              "Woolworths Market",
              BigDecimal.valueOf(1000),
              SourceType.MANUAL,
              account,
              user);
      transactionRepo.save(transaction);
    }

    BigDecimal totalCredit = transactionRepo.getTotalCreditThisMonth(user.getId());

    assertThat(totalCredit).isEqualByComparingTo(BigDecimal.valueOf(400));
  }

  @Test
  void testGetTotalCreditLastMonth() {
    int thisMonth = LocalDate.now().getMonthValue();
    int lastMonth = LocalDate.now().minusMonths(1).getMonthValue();

    LocalDate startLastMonth = LocalDate.now().minusMonths(1).withDayOfMonth(1);
    LocalDate endLastMonth = startLastMonth.withDayOfMonth(startLastMonth.lengthOfMonth());

    // Credit transactions for this month
    for (int i = 0; i < 4; i++) {
      Transaction transaction =
          new Transaction(
              LocalDate.of(2025, thisMonth, 1),
              BigDecimal.valueOf(100),
              "W4242 15/04",
              "Groceries",
              "Woolworths Market",
              BigDecimal.valueOf(1000),
              SourceType.MANUAL,
              account,
              user);
      transactionRepo.save(transaction);
    }
    // Debit debit transactions for last month
    for (int i = 0; i < 4; i++) {
      Transaction transaction =
          new Transaction(
              LocalDate.of(2025, lastMonth, 1),
              BigDecimal.valueOf(-100),
              "W4242 15/04",
              "Groceries",
              "Woolworths Market",
              BigDecimal.valueOf(1000),
              SourceType.MANUAL,
              account,
              user);
      transactionRepo.save(transaction);
    }
    // Credit transactions for last month
    for (int i = 0; i < 5; i++) {
      Transaction transaction =
          new Transaction(
              LocalDate.of(2025, lastMonth, 1),
              BigDecimal.valueOf(100),
              "W4242 15/04",
              "Groceries",
              "Woolworths Market",
              BigDecimal.valueOf(1000),
              SourceType.MANUAL,
              account,
              user);
      transactionRepo.save(transaction);
    }

    BigDecimal totalCredit =
        transactionRepo.getTotalCreditBetween(user.getId(), startLastMonth, endLastMonth);

    assertThat(totalCredit).isEqualByComparingTo(BigDecimal.valueOf(500));
  }

  @Test
  void testGetTotalDebitThisMonth() {
    int thisMonth = LocalDate.now().getMonthValue();
    int lastMonth = LocalDate.now().minusMonths(1).getMonthValue();

    // Credit transactions for this month
    for (int i = 0; i < 2; i++) {
      Transaction transaction =
          new Transaction(
              LocalDate.of(2025, thisMonth, 1),
              BigDecimal.valueOf(100),
              "W4242 15/04",
              "Groceries",
              "Woolworths Market",
              BigDecimal.valueOf(1000),
              SourceType.MANUAL,
              account,
              user);
      transactionRepo.save(transaction);
    }
    // Debit transactions for this month
    for (int i = 0; i < 3; i++) {
      Transaction transaction =
          new Transaction(
              LocalDate.of(2025, thisMonth, 1),
              BigDecimal.valueOf(-100),
              "W4242 15/04",
              "Groceries",
              "Woolworths Market",
              BigDecimal.valueOf(1000),
              SourceType.MANUAL,
              account,
              user);
      transactionRepo.save(transaction);
    }
    // Debit transactions for last month
    for (int i = 0; i < 2; i++) {
      Transaction transaction =
          new Transaction(
              LocalDate.of(2025, lastMonth, 1),
              BigDecimal.valueOf(-100),
              "W4242 15/04",
              "Groceries",
              "Woolworths Market",
              BigDecimal.valueOf(1000),
              SourceType.MANUAL,
              account,
              user);
      transactionRepo.save(transaction);
    }

    BigDecimal totalDebit = transactionRepo.getTotalDebitThisMonth(user.getId());

    assertThat(totalDebit).isEqualByComparingTo(BigDecimal.valueOf(-300));
  }

  @Test
  void testGetTotalDebitBetween() {
    int thisMonth = LocalDate.now().getMonthValue();
    int lastMonth = LocalDate.now().minusMonths(1).getMonthValue();

    LocalDate startLastMonth = LocalDate.now().minusMonths(1).withDayOfMonth(1);
    LocalDate endLastMonth = startLastMonth.withDayOfMonth(startLastMonth.lengthOfMonth());

    // Credit transactions for this month
    for (int i = 0; i < 2; i++) {
      Transaction transaction =
          new Transaction(
              LocalDate.of(2025, thisMonth, 1),
              BigDecimal.valueOf(100),
              "W4242 15/04",
              "Groceries",
              "Woolworths Market",
              BigDecimal.valueOf(1000),
              SourceType.MANUAL,
              account,
              user);
      transactionRepo.save(transaction);
    }
    // Debit transactions for this month
    for (int i = 0; i < 2; i++) {
      Transaction transaction =
          new Transaction(
              LocalDate.of(2025, thisMonth, 1),
              BigDecimal.valueOf(-100),
              "W4242 15/04",
              "Groceries",
              "Woolworths Market",
              BigDecimal.valueOf(1000),
              SourceType.MANUAL,
              account,
              user);
      transactionRepo.save(transaction);
    }
    // Debit transactions for last month
    for (int i = 0; i < 3; i++) {
      Transaction transaction =
          new Transaction(
              LocalDate.of(2025, lastMonth, 1),
              BigDecimal.valueOf(-100),
              "W4242 15/04",
              "Groceries",
              "Woolworths Market",
              BigDecimal.valueOf(1000),
              SourceType.MANUAL,
              account,
              user);
      transactionRepo.save(transaction);
    }

    BigDecimal totalDebit =
        transactionRepo.getTotalDebitBetween(user.getId(), startLastMonth, endLastMonth);

    assertThat(totalDebit).isEqualByComparingTo(BigDecimal.valueOf(-300));
  }

  @Test
  void testGetMonthlyIncomeForYear() {
    int thisYear = LocalDate.now().getYear();
    int lastYear = LocalDate.now().minusYears(1).getYear();

    List<BigDecimal> expectedMonthlyIncome = new ArrayList<>();
    int min = -800;
    int max = 800;

    for (int i = 0; i < 12; i++) {
      int randomIncome = new Random().nextInt((max - min) + 1) + min;
      expectedMonthlyIncome.add(BigDecimal.valueOf(randomIncome));
    }

    BigDecimal expectedSum =
        expectedMonthlyIncome.stream()
            .filter(value -> value.compareTo(BigDecimal.ZERO) > 0)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

    // Transactions for this year
    for (int monthIndex = 1; monthIndex <= 12; monthIndex++) {
      Transaction transaction =
          new Transaction(
              LocalDate.of(thisYear, monthIndex, 1),
              expectedMonthlyIncome.get(monthIndex - 1),
              "W4242 15/04",
              "Groceries",
              "Woolworths Market",
              BigDecimal.valueOf(1000),
              SourceType.MANUAL,
              account,
              user);
      transactionRepo.save(transaction);
    }
    // Transactions for last year
    for (int monthIndex = 1; monthIndex <= 12; monthIndex++) {
      Transaction transaction =
          new Transaction(
              LocalDate.of(lastYear, monthIndex, 1),
              expectedMonthlyIncome.get(monthIndex - 1),
              "W4242 15/04",
              "Groceries",
              "Woolworths Market",
              BigDecimal.valueOf(1000),
              SourceType.MANUAL,
              account,
              user);
      transactionRepo.save(transaction);
    }

    List<BigDecimal> monthlyIncomeForYear =
        transactionRepo.getMonthlyIncomeForYear(user.getId(), thisYear);
    BigDecimal actualSum = monthlyIncomeForYear.stream().reduce(BigDecimal.ZERO, BigDecimal::add);

    assertThat(actualSum).isEqualByComparingTo(expectedSum);
  }

  @Test
  void testFindTop5CategoriesByAmount() {
    List<ExpenseAnalysis> expectedCategories =
        List.of(
            new ExpenseAnalysis("Personal Care", BigDecimal.valueOf(1000)),
            new ExpenseAnalysis("Groceries", BigDecimal.valueOf(900)),
            new ExpenseAnalysis("Entertainment", BigDecimal.valueOf(800)),
            new ExpenseAnalysis("Uncategorised", BigDecimal.valueOf(700)),
            new ExpenseAnalysis("Transfers", BigDecimal.valueOf(600)),
            new ExpenseAnalysis("Restaurants", BigDecimal.valueOf(500)),
            new ExpenseAnalysis("Utility & Bills", BigDecimal.valueOf(400)));

    for (int i = 0; i < expectedCategories.size(); i++) {
      Transaction transaction =
          new Transaction(
              LocalDate.of(2025, 7, 1),
              expectedCategories.get(i).getValue(),
              "W4242 15/04",
              expectedCategories.get(i).getLabel(),
              "Store",
              BigDecimal.valueOf(1000),
              SourceType.MANUAL,
              account,
              user);
      transactionRepo.save(transaction);
    }

    List<ExpenseAnalysis> actualCategories =
        transactionRepo.findTop5CategoriesByAmount(user.getId());

    assertThat(actualCategories).hasSize(5);

    for (int i = 0; i < actualCategories.size(); i++) {
      assertThat(actualCategories.get(i).getLabel())
          .isEqualTo(expectedCategories.get(i).getLabel());
      assertThat(actualCategories.get(i).getValue())
          .isEqualByComparingTo(expectedCategories.get(i).getValue());
    }
  }

  @Test
  void testFindByUserIdOrderByDateAsc() {
    for (int i = 0; i < 3; i++) {
      transactionRepo.save(
          new Transaction(
              LocalDate.of(2025, 7, 5 - i), // descending order insertion
              BigDecimal.valueOf(50),
              "desc",
              "Test Category",
              "Merchant",
              BigDecimal.TEN,
              SourceType.MANUAL,
              account,
              user));
    }

    List<Transaction> result = transactionRepo.findByUser_IdOrderByDateAsc(user.getId());
    assertThat(result).isSortedAccordingTo(Comparator.comparing(Transaction::getDate));
  }

  @Test
  void testFindByUserIdOrderByDateDesc() {
    for (int i = 0; i < 3; i++) {
      transactionRepo.save(
          new Transaction(
              LocalDate.of(2025, 7, i + 1),
              BigDecimal.valueOf(50),
              "desc",
              "Test Category",
              "Merchant",
              BigDecimal.TEN,
              SourceType.MANUAL,
              account,
              user));
    }

    List<Transaction> result = transactionRepo.findByUser_IdOrderByDateDesc(user.getId());
    assertThat(result).isSortedAccordingTo(Comparator.comparing(Transaction::getDate).reversed());
  }

  @Test
  void testFindLatest3TransactionSummaries() {
    for (int i = 1; i <= 5; i++) {
      transactionRepo.save(
          new Transaction(
              LocalDate.of(2025, 7, i),
              BigDecimal.valueOf(100 + i),
              "Txn " + i,
              "Category",
              "Merchant",
              BigDecimal.TEN,
              SourceType.MANUAL,
              account,
              user));
    }

    List<RecentTransactions> summaries =
        transactionRepo.findLatest3TransactionSummaries(user.getId());

    assertThat(summaries).hasSize(3);
    assertThat(summaries.get(0).getDate()).isAfterOrEqualTo(summaries.get(1).getDate());
  }

  @Test
  void testSumAmountsByAccount() {
    transactionRepo.save(
        new Transaction(
            LocalDate.now(),
            BigDecimal.valueOf(100),
            "desc",
            "category",
            "merchant",
            BigDecimal.TEN,
            SourceType.MANUAL,
            account,
            user));

    transactionRepo.save(
        new Transaction(
            LocalDate.now(),
            BigDecimal.valueOf(200),
            "desc",
            "category",
            "merchant",
            BigDecimal.TEN,
            SourceType.MANUAL,
            account,
            user));

    BigDecimal sum = transactionRepo.sumAmountsByAccount(account);
    assertThat(sum).isEqualByComparingTo("300");
  }
}
