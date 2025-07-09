package com.shoxys.budgetbuddy_backend.Services;

import com.shoxys.budgetbuddy_backend.Entities.Account;
import com.shoxys.budgetbuddy_backend.Entities.Transaction;
import com.shoxys.budgetbuddy_backend.Entities.User;
import com.shoxys.budgetbuddy_backend.Enums.AccountType;
import com.shoxys.budgetbuddy_backend.Exceptions.AccountNotFoundException;
import com.shoxys.budgetbuddy_backend.Exceptions.UserNotFoundException;
import com.shoxys.budgetbuddy_backend.Repo.AccountRepo;
import com.shoxys.budgetbuddy_backend.Repo.SavingGoalsRepo;
import com.shoxys.budgetbuddy_backend.Repo.UserRepo;
import com.shoxys.budgetbuddy_backend.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.shoxys.budgetbuddy_backend.Enums.AccountType.SPENDING;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest  {
    @Mock
    private SavingGoalsRepo savingGoalsRepo;
    @Mock
    private AccountRepo accountRepo;
    @Mock
    private UserRepo userRepo;

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
        spendingAccount = new Account("Spending Account", SPENDING, null, BigDecimal.TEN, false, mockUser);
        spendingAccount.setId(SPENDING_ACCOUNT_ID);
        goalAccount.setId(GOAL_ACCOUNT_ID);
    }

    @Test
    public void getAccountsByUserId_shouldReturnAccountsForUser() {
        // Arrange
        List<Account> expectedUserAccounts = Arrays.asList(
                goalAccount,
                spendingAccount
        );

        when(userRepo.findById(USER_ID)).thenReturn(Optional.of(mockUser));
        when(accountRepo.findAccountsByUser(mockUser)).thenReturn(expectedUserAccounts);

        // Act
        List<Account> actualUserAccounts = accountService.getAccountsByUserId(USER_ID);

        // Assert
        TestUtils.assertListElementsMatch(expectedUserAccounts, actualUserAccounts, (expected, actual) -> {
            assertEquals(expected.getAccountNo(), actual.getAccountNo());
            assertEquals(expected.getBalance(), actual.getBalance());
            assertEquals(expected.getName(), actual.getName());
            assertEquals(expected.getType(), actual.getType());
            assertEquals(expected.getUser(), actual.getUser());
        });
    }

    @Test
    public void getAccountsByUserId_shouldThrowIfUserNotFound() {
        when(userRepo.findById(INVALID_USER_ID)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
                accountService.getAccountsByUserId(INVALID_USER_ID));
    }

    @Test
    public void upsertAccountBalance_shouldUpdateBalanceIfFound() {
        Account expectedAccount = spendingAccount;
        BigDecimal expectedNewBalance = BigDecimal.valueOf(1000);

        when(userRepo.findByEmail(VALID_EMAIL)).thenReturn(Optional.of(mockUser));
        when(accountRepo.findAccountByUserAndType(mockUser, SPENDING)).thenReturn(Optional.of(spendingAccount));

        Account resultAccount = accountService.upsertAccountBalance(VALID_EMAIL, expectedAccount.getName(), expectedAccount.getType(),  expectedNewBalance);

        assertEquals(expectedAccount.getName(), resultAccount.getName());
        assertEquals(expectedAccount.getType(), resultAccount.getType());
        assertEquals(expectedNewBalance, resultAccount.getBalance());
        verify(accountRepo).save(spendingAccount);
    }

    @Test
    public void upsertAccountBalance_shouldCreateAccountIfNotFound() {
        Account expectedAccount = spendingAccount;;
        BigDecimal expectedNewBalance = BigDecimal.valueOf(1000);

        when(userRepo.findByEmail(VALID_EMAIL)).thenReturn(Optional.of(mockUser));
        when(accountRepo.findAccountByUserAndType(mockUser, SPENDING)).thenReturn(Optional.empty());

        Account resultAccount = accountService.upsertAccountBalance(VALID_EMAIL, expectedAccount.getName(), expectedAccount.getType(),  expectedNewBalance);

        assertEquals(expectedAccount.getName(), resultAccount.getName());
        assertEquals(expectedAccount.getType(), resultAccount.getType());
        assertEquals(expectedNewBalance, resultAccount.getBalance());
        assertNull(resultAccount.getAccountNo());
        assertTrue(resultAccount.getIsManual());
        assertEquals(mockUser, resultAccount.getUser());
    }

    @Test
    public void upsertAccountBalance_shouldThrowIfUserNotFound() {
        Account expectedAccount = spendingAccount;;
        BigDecimal mockNewBalance = BigDecimal.valueOf(1000);

        when(userRepo.findByEmail(INVALID_EMAIL)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> accountService.upsertAccountBalance(INVALID_EMAIL, expectedAccount.getName(), expectedAccount.getType(),  mockNewBalance));

    }

    @Test
    public void upsertAccountBalance_shouldThrowIfNameNull() {
        Account expectedAccount = spendingAccount;;
        BigDecimal mockNewBalance = BigDecimal.valueOf(1000);

        when(userRepo.findByEmail(VALID_EMAIL)).thenReturn(Optional.of(mockUser));

        assertThrows(IllegalArgumentException.class,
                () -> accountService.upsertAccountBalance(
                        VALID_EMAIL, null, expectedAccount.getType(),  mockNewBalance));
    }

    @Test
    public void upsertAccountBalance_shouldThrowIfNameEmpty() {
        Account expectedAccount = spendingAccount;;
        BigDecimal mockNewBalance = BigDecimal.valueOf(1000);

        when(userRepo.findByEmail(VALID_EMAIL)).thenReturn(Optional.of(mockUser));

        assertThrows(IllegalArgumentException.class,
                () -> accountService.upsertAccountBalance(
                        VALID_EMAIL, "", expectedAccount.getType(),  mockNewBalance));
    }

    @Test
    public void upsertAccountBalance_shouldThrowIfTypeNull() {
        Account expectedAccount = spendingAccount;;
        BigDecimal mockNewBalance = BigDecimal.valueOf(1000);

        when(userRepo.findByEmail(VALID_EMAIL)).thenReturn(Optional.of(mockUser));

        assertThrows(IllegalArgumentException.class,
                () -> accountService.upsertAccountBalance(
                        VALID_EMAIL, expectedAccount.getName(), null,  mockNewBalance));
    }

    @Test
    public void upsertAccountBalance_shouldThrowIfNewBalanceNull() {
        Account expectedAccount = spendingAccount;;
        BigDecimal mockNewBalance = null;

        when(userRepo.findByEmail(VALID_EMAIL)).thenReturn(Optional.of(mockUser));

        assertThrows(IllegalArgumentException.class,
                () -> accountService.upsertAccountBalance(
                        VALID_EMAIL, expectedAccount.getName(), expectedAccount.getType(),  mockNewBalance));
    }

    @Test
    void createSpendingAccount_shouldSaveAndReturnCorrectAccount() {
        BigDecimal balance = BigDecimal.valueOf(250);

        when(accountRepo.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Account result = accountService.createSpendingAccount(mockUser, balance);

        assertEquals("Spending Account", result.getName());
        assertEquals(SPENDING, result.getType());
        assertEquals(balance, result.getBalance());
        assertTrue(result.getIsManual());
        assertEquals(mockUser, result.getUser());
    }

    @Test
    void syncSpendingAccountBalance_shouldAdjustBalanceCorrectly() {
        BigDecimal oldAmount = BigDecimal.valueOf(100);
        BigDecimal newAmount = BigDecimal.valueOf(150);
        BigDecimal expectedBalance = spendingAccount.getBalance().add(newAmount.subtract(oldAmount));

        Transaction transaction = new Transaction();
        transaction.setAccount(spendingAccount);
        transaction.setAmount(oldAmount);

        when(accountRepo.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Account result = accountService.syncSpendingAccountBalance(transaction, newAmount);

        assertEquals(expectedBalance, result.getBalance());
    }

    @Test
    void recalculateGoalSavingsBalance_shouldUpdateGoalAccountBalance() {
        when(accountRepo.findAccountByUserAndType(mockUser, AccountType.GOALSAVINGS)).thenReturn(Optional.of(goalAccount));
        when(savingGoalsRepo.sumContributionsByUser(mockUser)).thenReturn(BigDecimal.valueOf(300));
        when(accountRepo.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

        accountService.recalculateGoalSavingsBalance(mockUser);

        assertEquals(BigDecimal.valueOf(300), goalAccount.getBalance());
    }

    @Test
    void recalculateGoalSavingsBalance_shouldThrowIfAccountNotFound() {
        when(accountRepo.findAccountByUserAndType(mockUser, AccountType.GOALSAVINGS)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class,
                () -> accountService.recalculateGoalSavingsBalance(mockUser));
    }

    @Test
    void handleAccountNoAssign_shouldReturnExistingAccount() {
        when(accountRepo.findByAccountNo(1001)).thenReturn(Optional.of(spendingAccount));

        Account result = accountService.handleAccountNoAssign(1001, mockUser);

        assertEquals(spendingAccount, result);
    }

    @Test
    void handleAccountNoAssign_shouldCreateNewAccountIfNotFound() {
        int accountNo = 2222;

        when(accountRepo.findByAccountNo(accountNo)).thenReturn(Optional.empty());
        when(accountRepo.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Account result = accountService.handleAccountNoAssign(accountNo, mockUser);

        assertEquals(accountNo, result.getAccountNo());
        assertEquals(SPENDING, result.getType());
        assertEquals("Spending Account", result.getName());
        assertEquals(mockUser, result.getUser());
    }

}
