package com.shoxys.budgetbuddy_backend.Repo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.shoxys.budgetbuddy_backend.DTOs.Dashboard.ExpenseAnalysis;
import com.shoxys.budgetbuddy_backend.DTOs.Dashboard.RecentTransactions;
import com.shoxys.budgetbuddy_backend.Entities.Account;
import com.shoxys.budgetbuddy_backend.Entities.Transaction;
import com.shoxys.budgetbuddy_backend.Entities.User;
import com.shoxys.budgetbuddy_backend.Enums.AccountType;
import com.shoxys.budgetbuddy_backend.Enums.SourceType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@DataJpaTest
public class TransactionRepoTest {

  @Autowired private TransactionRepo transactionRepo;

  @Autowired private UserRepo userRepo;

  @Autowired private AccountRepo accountRepo;

  private User user;
  private Account account;

  @BeforeEach
  void setUp() {
    // Create and save user
    user = new User();
    user.setEmail("test@example.com");
    user.setHashedPassword("secret");
    user = userRepo.save(user);

    // Create and save account associated with user
    account = new Account();
    account.setName("Test Account");
    account.setType(AccountType.SPENDING);
    account.setBalance(BigDecimal.valueOf(1000));
    account.setManual(true);
    account.setUser(user); // Associate account with user
    account = accountRepo.save(account);
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

    LocalDate startDate = LocalDate.of(2025, 4, expectedDays.get(0));
    LocalDate endDate = LocalDate.of(2025, 4, expectedDays.get(expectedDays.size() - 1));

    // Create transactions with varied dates
    for (int i = 1; i <= TOTAL_TRANSACTIONS; i++) {
      Transaction transaction =
          new Transaction(
              LocalDate.of(2025, 4, i),
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
    int thisMonth = 7; // Fixed month for consistency
    int lastMonth = 6;

    // Debit transactions for this month
    for (int i = 0; i < 4; i++) {
      transactionRepo.save(
          new Transaction(
              LocalDate.of(2025, thisMonth, 1),
              BigDecimal.valueOf(-100),
              "W4242 15/04",
              "Groceries",
              "Woolworths Market",
              BigDecimal.valueOf(1000),
              SourceType.MANUAL,
              account,
              user));
    }

    // Credit transactions for this month
    for (int i = 0; i < 4; i++) {
      transactionRepo.save(
          new Transaction(
              LocalDate.of(2025, thisMonth, 1),
              BigDecimal.valueOf(100),
              "W4242 15/04",
              "Salary",
              "Employer",
              BigDecimal.valueOf(1000),
              SourceType.MANUAL,
              account,
              user));
    }

    // Credit transactions for last month
    for (int i = 0; i < 5; i++) {
      transactionRepo.save(
          new Transaction(
              LocalDate.of(2025, lastMonth, 1),
              BigDecimal.valueOf(100),
              "W4242 15/04",
              "Salary",
              "Employer",
              BigDecimal.valueOf(1000),
              SourceType.MANUAL,
              account,
              user));
    }

    BigDecimal totalCredit = transactionRepo.getTotalCreditThisMonth(user.getId());
    assertThat(totalCredit).isEqualByComparingTo(BigDecimal.valueOf(400));
  }

  @Test
  void testGetTotalCreditBetween() {
    int lastMonth = 6;
    LocalDate startLastMonth = LocalDate.of(2025, lastMonth, 1);
    LocalDate endLastMonth = startLastMonth.withDayOfMonth(startLastMonth.lengthOfMonth());

    // Credit transactions for last month
    for (int i = 0; i < 5; i++) {
      transactionRepo.save(
          new Transaction(
              LocalDate.of(2025, lastMonth, 1 + i),
              BigDecimal.valueOf(100),
              "W4242 15/04",
              "Salary",
              "Employer",
              BigDecimal.valueOf(1000),
              SourceType.MANUAL,
              account,
              user));
    }

    // Debit transactions for last month
    for (int i = 0; i < 4; i++) {
      transactionRepo.save(
          new Transaction(
              LocalDate.of(2025, lastMonth, 1 + i),
              BigDecimal.valueOf(-100),
              "W4242 15/04",
              "Groceries",
              "Woolworths Market",
              BigDecimal.valueOf(1000),
              SourceType.MANUAL,
              account,
              user));
    }

    BigDecimal totalCredit =
        transactionRepo.getTotalCreditBetween(user.getId(), startLastMonth, endLastMonth);
    assertThat(totalCredit).isEqualByComparingTo(BigDecimal.valueOf(500));
  }

  @Test
  void testGetTotalDebitThisMonth() {
    int thisMonth = 7;
    int lastMonth = 6;

    // Credit transactions for this month
    for (int i = 0; i < 2; i++) {
      transactionRepo.save(
          new Transaction(
              LocalDate.of(2025, thisMonth, 1),
              BigDecimal.valueOf(100),
              "W4242 15/04",
              "Salary",
              "Employer",
              BigDecimal.valueOf(1000),
              SourceType.MANUAL,
              account,
              user));
    }

    // Debit transactions for this month
    for (int i = 0; i < 3; i++) {
      transactionRepo.save(
          new Transaction(
              LocalDate.of(2025, thisMonth, 1),
              BigDecimal.valueOf(-100),
              "W4242 15/04",
              "Groceries",
              "Woolworths Market",
              BigDecimal.valueOf(1000),
              SourceType.MANUAL,
              account,
              user));
    }

    // Debit transactions for last month
    for (int i = 0; i < 2; i++) {
      transactionRepo.save(
          new Transaction(
              LocalDate.of(2025, lastMonth, 1),
              BigDecimal.valueOf(-100),
              "W4242 15/04",
              "Groceries",
              "Woolworths Market",
              BigDecimal.valueOf(1000),
              SourceType.MANUAL,
              account,
              user));
    }

    BigDecimal totalDebit = transactionRepo.getTotalDebitThisMonth(user.getId());
    assertThat(totalDebit).isEqualByComparingTo(BigDecimal.valueOf(-300));
  }

  @Test
  void testGetTotalDebitBetween() {
    int lastMonth = 6;
    LocalDate startLastMonth = LocalDate.of(2025, lastMonth, 1);
    LocalDate endLastMonth = startLastMonth.withDayOfMonth(startLastMonth.lengthOfMonth());

    // Credit transactions for last month
    for (int i = 0; i < 2; i++) {
      transactionRepo.save(
          new Transaction(
              LocalDate.of(2025, lastMonth, 1 + i),
              BigDecimal.valueOf(100),
              "W4242 15/04",
              "Salary",
              "Employer",
              BigDecimal.valueOf(1000),
              SourceType.MANUAL,
              account,
              user));
    }

    // Debit transactions for last month
    for (int i = 0; i < 3; i++) {
      transactionRepo.save(
          new Transaction(
              LocalDate.of(2025, lastMonth, 1 + i),
              BigDecimal.valueOf(-100),
              "W4242 15/04",
              "Groceries",
              "Woolworths Market",
              BigDecimal.valueOf(1000),
              SourceType.MANUAL,
              account,
              user));
    }

    BigDecimal totalDebit =
        transactionRepo.getTotalDebitBetween(user.getId(), startLastMonth, endLastMonth);
    assertThat(totalDebit).isEqualByComparingTo(BigDecimal.valueOf(-300));
  }

  @Test
  void testGetMonthlyIncomeForYear() {
    int year = 2025;

    // Create transactions for specific months
    List<BigDecimal> expectedIncomes =
        List.of(
            BigDecimal.valueOf(100), // January
            BigDecimal.valueOf(200), // February
            BigDecimal.valueOf(300) // March
            );

    for (int month = 1; month <= 3; month++) {
      transactionRepo.save(
          new Transaction(
              LocalDate.of(year, month, 1),
              expectedIncomes.get(month - 1),
              "W4242 15/04",
              "Salary",
              "Employer",
              BigDecimal.valueOf(1000),
              SourceType.MANUAL,
              account,
              user));
      // Add a debit transaction to ensure only credits are summed
      transactionRepo.save(
          new Transaction(
              LocalDate.of(year, month, 1),
              BigDecimal.valueOf(-50),
              "W4242 15/04",
              "Groceries",
              "Woolworths Market",
              BigDecimal.valueOf(1000),
              SourceType.MANUAL,
              account,
              user));
    }

    List<Object[]> monthlyIncome = transactionRepo.getMonthlyIncomeForYear(user.getId(), year);
    assertThat(monthlyIncome).hasSize(3);

    for (int i = 0; i < monthlyIncome.size(); i++) {
      Object[] result = monthlyIncome.get(i);
      assertThat(result[0]).isEqualTo(i + 1); // Month
      assertEquals(
          0,
          ((BigDecimal) result[1]).compareTo(expectedIncomes.get(i)),
          "BigDecimal values should be numerically equal"); // Income
    }
  }

  @Test
  void testFindTop5ExpenseCategoriesByAmount() {
    List<ExpenseAnalysis> expectedCategories =
        List.of(
            new ExpenseAnalysis("Personal Care", BigDecimal.valueOf(-1000)),
            new ExpenseAnalysis("Groceries", BigDecimal.valueOf(-900)),
            new ExpenseAnalysis("Entertainment", BigDecimal.valueOf(-800)),
            new ExpenseAnalysis("Uncategorised", BigDecimal.valueOf(-700)),
            new ExpenseAnalysis("Restaurants", BigDecimal.valueOf(-500)));

    for (ExpenseAnalysis category : expectedCategories) {
      transactionRepo.save(
          new Transaction(
              LocalDate.of(2025, 7, 1),
              category.getValue(),
              "W4242 15/04",
              category.getLabel(),
              "Store",
              BigDecimal.valueOf(1000),
              SourceType.MANUAL,
              account,
              user));
    }

    // Add a transfer transaction that should be excluded
    transactionRepo.save(
        new Transaction(
            LocalDate.of(2025, 7, 1),
            BigDecimal.valueOf(-600),
            "W4242 15/04",
            "Transfer",
            "Bank",
            BigDecimal.valueOf(1000),
            SourceType.MANUAL,
            account,
            user));

    List<ExpenseAnalysis> actualCategories =
        transactionRepo.findTop5ExpenseCategoriesByAmount(user.getId());
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
    for (int i = 1; i <= 3; i++) {
      transactionRepo.save(
          new Transaction(
              LocalDate.of(2025, 7, i),
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
    assertThat(result).hasSize(3);
    assertThat(result).isSortedAccordingTo(Comparator.comparing(Transaction::getDate));
  }

  @Test
  void testFindByUserIdOrderByDateDesc() {
    for (int i = 1; i <= 3; i++) {
      transactionRepo.save(
          new Transaction(
              LocalDate.of(2025, 7, i),
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
    assertThat(result).hasSize(3);
    assertThat(result).isSortedAccordingTo(Comparator.comparing(Transaction::getDate).reversed());
  }

  @Test
  void testFindByUserIdOrderByDateAscPageable() {
    for (int i = 1; i <= 5; i++) {
      transactionRepo.save(
          new Transaction(
              LocalDate.of(2025, 7, i),
              BigDecimal.valueOf(50),
              "desc",
              "Test Category",
              "Merchant",
              BigDecimal.TEN,
              SourceType.MANUAL,
              account,
              user));
    }

    Pageable pageable = PageRequest.of(0, 3);
    Page<Transaction> result = transactionRepo.findByUser_IdOrderByDateAsc(user.getId(), pageable);
    assertThat(result.getContent()).hasSize(3);
    assertThat(result.getTotalElements()).isEqualTo(5);
    assertThat(result.getContent()).isSortedAccordingTo(Comparator.comparing(Transaction::getDate));
  }

  @Test
  void testFindByUserIdOrderByDateDescPageable() {
    for (int i = 1; i <= 5; i++) {
      transactionRepo.save(
          new Transaction(
              LocalDate.of(2025, 7, i),
              BigDecimal.valueOf(50),
              "desc",
              "Test Category",
              "Merchant",
              BigDecimal.TEN,
              SourceType.MANUAL,
              account,
              user));
    }

    Pageable pageable = PageRequest.of(0, 3);
    Page<Transaction> result = transactionRepo.findByUser_IdOrderByDateDesc(user.getId(), pageable);
    assertThat(result.getContent()).hasSize(3);
    assertThat(result.getTotalElements()).isEqualTo(5);
    assertThat(result.getContent())
        .isSortedAccordingTo(Comparator.comparing(Transaction::getDate).reversed());
  }

  @Test
  void testFindByAccountOrderByDateDesc() {
    for (int i = 1; i <= 3; i++) {
      transactionRepo.save(
          new Transaction(
              LocalDate.of(2025, 7, i),
              BigDecimal.valueOf(50),
              "desc",
              "Test Category",
              "Merchant",
              BigDecimal.TEN,
              SourceType.MANUAL,
              account,
              user));
    }

    List<Transaction> result = transactionRepo.findByAccountOrderByDateDesc(account);
    assertThat(result).hasSize(3);
    assertThat(result).isSortedAccordingTo(Comparator.comparing(Transaction::getDate).reversed());
  }

  @Test
  void testFindTransactionByUserAndId() {
    Transaction transaction =
        new Transaction(
            LocalDate.of(2025, 7, 1),
            BigDecimal.valueOf(100),
            "desc",
            "Test Category",
            "Merchant",
            BigDecimal.TEN,
            SourceType.MANUAL,
            account,
            user);
    transaction = transactionRepo.save(transaction);

    Optional<Transaction> result =
        transactionRepo.findTransactionByUserAndId(user, transaction.getId());
    assertThat(result).isPresent();
    assertThat(result.get().getId()).isEqualTo(transaction.getId());
    assertThat(result.get().getUser()).isEqualTo(user);
  }

  @Test
  void testDeleteAllByIdInAndUser() {
    Transaction transaction1 =
        new Transaction(
            LocalDate.of(2025, 7, 1),
            BigDecimal.valueOf(100),
            "desc",
            "Test Category",
            "Merchant",
            BigDecimal.TEN,
            SourceType.MANUAL,
            account,
            user);
    Transaction transaction2 =
        new Transaction(
            LocalDate.of(2025, 7, 2),
            BigDecimal.valueOf(200),
            "desc",
            "Test Category",
            "Merchant",
            BigDecimal.TEN,
            SourceType.MANUAL,
            account,
            user);
    transaction1 = transactionRepo.save(transaction1);
    transaction2 = transactionRepo.save(transaction2);

    List<Long> idsToDelete = List.of(transaction1.getId(), transaction2.getId());
    transactionRepo.deleteAllByIdInAndUser(idsToDelete, user);

    assertThat(transactionRepo.findById(transaction1.getId())).isEmpty();
    assertThat(transactionRepo.findById(transaction2.getId())).isEmpty();
  }

  @Test
  void testFindLatest3TransactionSummaries() {
    for (int i = 1; i <= 5; i++) {
      transactionRepo.save(
          new Transaction(
              LocalDate.of(2025, 7, i),
              BigDecimal.valueOf(100 + i),
              "Txn " + i,
              "Category" + i,
              "Merchant" + i,
              BigDecimal.TEN,
              SourceType.MANUAL,
              account,
              user));
    }

    List<RecentTransactions> summaries =
        transactionRepo.findLatest3TransactionSummaries(user.getId());
    assertThat(summaries).hasSize(3);
    assertThat(summaries)
        .isSortedAccordingTo(Comparator.comparing(RecentTransactions::getDate).reversed());
    assertThat(summaries.get(0).getDescription()).isEqualTo("Txn 5");
    assertThat(summaries.get(0).getCategory()).isEqualTo("Category5");
    assertThat(summaries.get(0).getAmount()).isEqualByComparingTo(BigDecimal.valueOf(105));
  }

  @Test
  void testSumAmountsByAccount() {
    transactionRepo.save(
        new Transaction(
            LocalDate.of(2025, 7, 1),
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
            LocalDate.of(2025, 7, 2),
            BigDecimal.valueOf(200),
            "desc",
            "category",
            "merchant",
            BigDecimal.TEN,
            SourceType.MANUAL,
            account,
            user));

    BigDecimal sum = transactionRepo.sumAmountsByAccount(account);
    assertThat(sum).isEqualByComparingTo(BigDecimal.valueOf(300));
  }
}
