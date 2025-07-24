package com.shoxys.budgetbuddy_backend.Services;

import static com.shoxys.budgetbuddy_backend.Enums.AccountType.SPENDING;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
import com.shoxys.budgetbuddy_backend.TestUtils;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

  @Mock
  private SavingGoalsRepo savingGoalsRepo;
  @Mock
  private AccountRepo accountRepo;
  @Mock
  private UserRepo userRepo;
  @Mock
  private TransactionRepo transactionRepo;

  @InjectMocks
  private AccountService accountService;

  private final String VALID_EMAIL = "valid@example.com";
  private final String INVALID_EMAIL = "invalid@example.com";

  private static final long GOAL_ACCOUNT_ID = 1L;
  private static final long SPENDING_ACCOUNT_ID = 2L;
  private static final long USER_ID = 1L;
  private static final long INVALID_USER_ID = -1L;

  private User mockUser;
  private Account goalAccount;
  private Account spendingAccount;

  @BeforeEach
  void setup() {
    mockUser = new User(VALID_EMAIL, "hashedpass");
    mockUser.setId(USER_ID);

    goalAccount = new Account("Goal Savings", AccountType.GOALSAVINGS, null, BigDecimal.TEN, true, mockUser);
    goalAccount.setId(GOAL_ACCOUNT_ID);
    spendingAccount = new Account("Spending Account", SPENDING, null, BigDecimal.TEN, false, mockUser);
    spendingAccount.setId(SPENDING_ACCOUNT_ID);
  }

  @Test
  public void getAccountsByUserId_shouldReturnAccountsForUser() {
    // Arrange
    List<Account> expectedUserAccounts = Arrays.asList(goalAccount, spendingAccount);
    when(userRepo.findById(USER_ID)).thenReturn(Optional.of(mockUser));
    when(accountRepo.findAccountsByUser(mockUser)).thenReturn(expectedUserAccounts);

    // Act
    List<Account> actualUserAccounts = accountService.getAccountsByUserId(USER_ID);

    // Assert
    TestUtils.assertListElementsMatch(
            expectedUserAccounts,
            actualUserAccounts,
            (expected, actual) -> {
              assertEquals(expected.getAccountNo(), actual.getAccountNo());
              assertEquals(expected.getBalance(), actual.getBalance());
              assertEquals(expected.getName(), actual.getName());
              assertEquals(expected.getType(), actual.getType());
              assertEquals(expected.getUser(), actual.getUser());
            });
  }

  @Test
  public void getAccountsByUserId_shouldThrowIfUserNotFound() {
    // Arrange
    Long noneExistingUserId = 9999L;
    when(userRepo.findById(noneExistingUserId)).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(UserNotFoundException.class, () -> accountService.getAccountsByUserId(noneExistingUserId));
  }

  @Test
  public void upsertAccountBalance_shouldUpdateBalanceIfFoundById() {
    // Arrange
    BigDecimal expectedNewBalance = BigDecimal.valueOf(1000);
    when(userRepo.findByEmail(VALID_EMAIL)).thenReturn(Optional.of(mockUser));
    when(accountRepo.findByIdAndUser(SPENDING_ACCOUNT_ID, mockUser)).thenReturn(Optional.of(spendingAccount));
    when(accountRepo.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

    // Act
    Account resultAccount = accountService.upsertAccountBalance(
            VALID_EMAIL, SPENDING_ACCOUNT_ID, spendingAccount.getName(), SPENDING, expectedNewBalance);

    // Assert
    assertEquals(spendingAccount.getName(), resultAccount.getName());
    assertEquals(SPENDING, resultAccount.getType());
    assertEquals(0, expectedNewBalance.compareTo(resultAccount.getBalance()));
    verify(accountRepo).save(spendingAccount);
  }

  @Test
  public void upsertAccountBalance_shouldUpdateBalanceIfFoundByNameAndType() {
    // Arrange
    BigDecimal expectedNewBalance = BigDecimal.valueOf(1000);
    when(userRepo.findByEmail(VALID_EMAIL)).thenReturn(Optional.of(mockUser));
    when(accountRepo.findByUserAndNameAndType(mockUser, spendingAccount.getName(), SPENDING))
            .thenReturn(Optional.of(spendingAccount));
    when(accountRepo.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

    // Act
    Account resultAccount = accountService.upsertAccountBalance(
            VALID_EMAIL, null, spendingAccount.getName(), SPENDING, expectedNewBalance);

    // Assert
    assertEquals(spendingAccount.getName(), resultAccount.getName());
    assertEquals(SPENDING, resultAccount.getType());
    assertEquals(0, expectedNewBalance.compareTo(resultAccount.getBalance()));
    verify(accountRepo).save(spendingAccount);
  }

  @Test
  public void upsertAccountBalance_shouldCreateAccountIfNotFound() {
    // Arrange
    BigDecimal expectedNewBalance = BigDecimal.valueOf(1000);
    when(userRepo.findByEmail(VALID_EMAIL)).thenReturn(Optional.of(mockUser));
    when(accountRepo.findByUserAndNameAndType(mockUser, spendingAccount.getName(), SPENDING))
            .thenReturn(Optional.empty());
    when(accountRepo.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

    // Act
    Account resultAccount = accountService.upsertAccountBalance(
            VALID_EMAIL, null, spendingAccount.getName(), SPENDING, expectedNewBalance);

    // Assert
    assertEquals(spendingAccount.getName(), resultAccount.getName());
    assertEquals(SPENDING, resultAccount.getType());
    assertEquals(0, expectedNewBalance.compareTo(resultAccount.getBalance()));
    assertNull(resultAccount.getAccountNo());
    assertTrue(resultAccount.isManual());
    assertEquals(mockUser, resultAccount.getUser());
    verify(accountRepo).save(any(Account.class));
  }

  @Test
  public void upsertAccountBalance_shouldThrowIfUserNotFound() {
    // Arrange
    BigDecimal mockNewBalance = BigDecimal.valueOf(1000);
    when(userRepo.findByEmail(INVALID_EMAIL)).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(UserNotFoundException.class, () ->
            accountService.upsertAccountBalance(INVALID_EMAIL, null, spendingAccount.getName(), SPENDING, mockNewBalance));
  }

  @Test
  public void upsertAccountBalance_shouldThrowIfNameNull() {
    // Arrange
    BigDecimal mockNewBalance = BigDecimal.valueOf(1000);

    // Act & Assert
    assertThrows(IllegalArgumentException.class, () ->
            accountService.upsertAccountBalance(VALID_EMAIL, null, null, SPENDING, mockNewBalance));
  }

  @Test
  public void upsertAccountBalance_shouldThrowIfNameEmpty() {
    // Arrange
    BigDecimal mockNewBalance = BigDecimal.valueOf(1000);
    // Act & Assert
    assertThrows(IllegalArgumentException.class, () ->
            accountService.upsertAccountBalance(VALID_EMAIL, null, "", SPENDING, mockNewBalance));
  }

  @Test
  public void upsertAccountBalance_shouldThrowIfTypeNull() {
    // Arrange
    BigDecimal mockNewBalance = BigDecimal.valueOf(1000);

    // Act & Assert
    assertThrows(IllegalArgumentException.class, () ->
            accountService.upsertAccountBalance(VALID_EMAIL, null, spendingAccount.getName(), null, mockNewBalance));
  }

  @Test
  public void upsertAccountBalance_shouldThrowIfNewBalanceNull() {
    // Arrange

    // Act & Assert
    assertThrows(IllegalArgumentException.class, () ->
            accountService.upsertAccountBalance(VALID_EMAIL, null, spendingAccount.getName(), SPENDING, null));
  }

  @Test
  public void createSpendingAccount_shouldSaveAndReturnCorrectAccount() {
    // Arrange
    BigDecimal balance = BigDecimal.valueOf(250);
    when(accountRepo.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

    // Act
    Account result = accountService.createSpendingAccount(mockUser, balance);

    // Assert
    assertEquals("Spending Account", result.getName());
    assertEquals(SPENDING, result.getType());
    assertEquals(0, balance.compareTo(result.getBalance()));
    assertTrue(result.isManual());
    assertEquals(mockUser, result.getUser());
    verify(accountRepo).save(any(Account.class));
  }

  @Test
  public void createGoalSavingsAccount_shouldSaveAndReturnCorrectAccount() {
    // Arrange
    BigDecimal balance = BigDecimal.valueOf(500);
    when(accountRepo.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

    // Act
    Account result = accountService.createGoalSavingsAccount(mockUser, balance);

    // Assert
    assertEquals("Goal Savings", result.getName());
    assertEquals(AccountType.GOALSAVINGS, result.getType());
    assertEquals(0, balance.compareTo(result.getBalance()));
    assertTrue(result.isManual());
    assertEquals(mockUser, result.getUser());
    verify(accountRepo).save(any(Account.class));
  }

  @Test
  public void syncSpendingAccountBalance_shouldAdjustBalanceCorrectly() {
    // Arrange
    BigDecimal oldAmount = BigDecimal.valueOf(100);
    BigDecimal newAmount = BigDecimal.valueOf(150);
    BigDecimal expectedBalance = spendingAccount.getBalance().add(newAmount.subtract(oldAmount));
    Transaction transaction = new Transaction();
    transaction.setId(1L);
    transaction.setAccount(spendingAccount);
    transaction.setAmount(oldAmount);
    when(accountRepo.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

    // Act
    Account result = accountService.syncSpendingAccountBalance(transaction, newAmount);

    // Assert
    assertEquals(0, expectedBalance.compareTo(result.getBalance()));
    verify(accountRepo).save(spendingAccount);
  }

  @Test
  public void updateAccountBalance_shouldUpdateBalance() {
    // Arrange
    BigDecimal newBalance = BigDecimal.valueOf(2000);
    when(accountRepo.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

    // Act
    accountService.updateAccountBalance(spendingAccount, newBalance);

    // Assert
    assertEquals(0, newBalance.compareTo(spendingAccount.getBalance()));
    verify(accountRepo).save(spendingAccount);
  }

  @Test
  public void getAccountBalance_shouldReturnBalanceIfFound() {
    // Arrange
    when(userRepo.findByEmail(VALID_EMAIL)).thenReturn(Optional.of(mockUser));
    when(accountRepo.findByUserAndNameAndType(mockUser, spendingAccount.getName(), SPENDING))
            .thenReturn(Optional.of(spendingAccount));

    // Act
    BigDecimal balance = accountService.getAccountBalance(VALID_EMAIL, spendingAccount.getName(), SPENDING);

    // Assert
    assertEquals(0, BigDecimal.TEN.compareTo(balance));
  }

  @Test
  public void getAccountBalance_shouldReturnNullIfNotFound() {
    // Arrange
    when(userRepo.findByEmail(VALID_EMAIL)).thenReturn(Optional.of(mockUser));
    when(accountRepo.findByUserAndNameAndType(mockUser, spendingAccount.getName(), SPENDING))
            .thenReturn(Optional.empty());

    // Act
    BigDecimal balance = accountService.getAccountBalance(VALID_EMAIL, spendingAccount.getName(), SPENDING);

    // Assert
    assertNull(balance);
  }

  @Test
  public void getAccountBalance_shouldThrowIfUserNotFound() {
    // Arrange
    when(userRepo.findByEmail(INVALID_EMAIL)).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(UserNotFoundException.class, () ->
            accountService.getAccountBalance(INVALID_EMAIL, spendingAccount.getName(), SPENDING));
  }

  @Test
  public void recalculateBalanceForSpendingAccount_shouldUpdateBalanceIfTransactionExists() {
    // Arrange
    BigDecimal transactionBalance = BigDecimal.valueOf(500);
    Transaction transaction = new Transaction();
    transaction.setAccount(spendingAccount);
    transaction.setBalanceAtTransaction(transactionBalance);
    transaction.setDate(LocalDate.of(2025, 7, 1));
    when(transactionRepo.findByAccountOrderByDateDesc(spendingAccount)).thenReturn(List.of(transaction));
    when(accountRepo.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

    // Act
    accountService.recalculateBalanceForSpendingAccount(spendingAccount);

    // Assert
    assertEquals(0, transactionBalance.compareTo(spendingAccount.getBalance()));
    verify(accountRepo).save(spendingAccount);
  }

  @Test
  public void recalculateBalanceForSpendingAccount_shouldNotUpdateBalanceIfNoTransactions() {
    // Arrange
    BigDecimal originalBalance = spendingAccount.getBalance();
    when(transactionRepo.findByAccountOrderByDateDesc(spendingAccount)).thenReturn(List.of());

    // Act
    accountService.recalculateBalanceForSpendingAccount(spendingAccount);

    // Assert
    assertEquals(0, originalBalance.compareTo(spendingAccount.getBalance()));
    verify(accountRepo, never()).save(any(Account.class));
  }

  @Test
  public void recalculateGoalSavingsBalance_shouldUpdateGoalAccountBalance() {
    // Arrange
    BigDecimal totalContributed = BigDecimal.valueOf(300);
    when(accountRepo.findAccountByUserAndType(mockUser, AccountType.GOALSAVINGS))
            .thenReturn(Optional.of(goalAccount));
    when(savingGoalsRepo.sumContributionsByUser(mockUser)).thenReturn(totalContributed);
    when(accountRepo.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

    // Act
    accountService.recalculateGoalSavingsBalance(mockUser);

    // Assert
    assertEquals(0, totalContributed.compareTo(goalAccount.getBalance()));
    verify(accountRepo).save(goalAccount);
  }

  @Test
  public void recalculateGoalSavingsBalance_shouldThrowIfAccountNotFound() {
    // Arrange
    when(accountRepo.findAccountByUserAndType(mockUser, AccountType.GOALSAVINGS))
            .thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(AccountNotFoundException.class, () -> accountService.recalculateGoalSavingsBalance(mockUser));
  }

  @Test
  public void handleFetchAccount_shouldReturnExistingAccount() {
    // Arrange
    when(accountRepo.findAccountByUserAndType(mockUser, SPENDING)).thenReturn(Optional.of(spendingAccount));

    // Act
    Account result = accountService.handleFetchAccount(mockUser);

    // Assert
    assertEquals(spendingAccount, result);
    verify(accountRepo, never()).save(any(Account.class));
  }

  @Test
  public void handleFetchAccount_shouldCreateNewAccountIfNotFound() {
    // Arrange
    when(accountRepo.findAccountByUserAndType(mockUser, SPENDING)).thenReturn(Optional.empty());
    when(accountRepo.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

    // Act
    Account result = accountService.handleFetchAccount(mockUser);

    // Assert
    assertEquals("Spending Account", result.getName());
    assertEquals(SPENDING, result.getType());
    assertEquals(0, BigDecimal.ZERO.compareTo(result.getBalance()));
    assertTrue(result.isManual());
    assertEquals(mockUser, result.getUser());
    verify(accountRepo).save(any(Account.class));
  }
}