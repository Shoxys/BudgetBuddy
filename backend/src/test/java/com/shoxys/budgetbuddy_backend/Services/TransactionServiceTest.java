package com.shoxys.budgetbuddy_backend.Services;

import com.shoxys.budgetbuddy_backend.DTOs.TransactionRequest;
import com.shoxys.budgetbuddy_backend.DTOs.TransactionSummaryResponse;
import com.shoxys.budgetbuddy_backend.Entities.Account;
import com.shoxys.budgetbuddy_backend.Entities.Transaction;
import com.shoxys.budgetbuddy_backend.Entities.User;
import com.shoxys.budgetbuddy_backend.Enums.SourceType;
import com.shoxys.budgetbuddy_backend.Exceptions.AccountNotFoundException;
import com.shoxys.budgetbuddy_backend.Exceptions.InvalidDateRangeException;
import com.shoxys.budgetbuddy_backend.Exceptions.TransactionNotFoundException;
import com.shoxys.budgetbuddy_backend.Exceptions.UserNotFoundException;
import com.shoxys.budgetbuddy_backend.Repo.AccountRepo;
import com.shoxys.budgetbuddy_backend.Repo.TransactionRepo;
import com.shoxys.budgetbuddy_backend.Repo.UserRepo;
import com.shoxys.budgetbuddy_backend.TestUtils;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static com.shoxys.budgetbuddy_backend.Enums.AccountType.SPENDING;
import static com.shoxys.budgetbuddy_backend.Enums.CsvSampleData.VALID_SAMPLE;
import static com.shoxys.budgetbuddy_backend.Enums.TransactionType.DEBIT;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @Mock
    private TransactionRepo transactionRepo;
    @Mock
    private AccountRepo accountRepo;
    @Mock
    private UserRepo userRepo;
    @Mock
    private AccountService accountService;

    @InjectMocks
    private TransactionService transactionService;

    private static final User validUser = new User("test@example.com", "PasswordHash123");
    private static final String validUserEmail = validUser.getEmail();
    private static final String invalidUserEmail = "invalid@example.com";

    private static final long invalidUserId = 999L;
    private static final Account mockAccount = new Account();

    private static final Map<String, Transaction> transactionMap = new HashMap<>();
    static {

        transactionMap.put("Transaction1", new Transaction(
                LocalDate.of(2025, 4, 15),
                BigDecimal.valueOf(12.5),
                "W4242 15/04",
                "Groceries",
                "Woolworths Market",
                BigDecimal.valueOf(1200),
                SourceType.MANUAL,
                mockAccount,
                validUser
        ));

        transactionMap.put("Transaction2", new Transaction(
                LocalDate.of(2025, 4, 16),
                BigDecimal.valueOf(-1500.00),
                "P1234 16/04",
                "Salary",
                "Company Payroll",
                BigDecimal.valueOf(2700),
                SourceType.MANUAL,
                mockAccount,
                validUser
        ));
        transactionMap.put("Transaction3", new Transaction(
                LocalDate.of(2025, 4, 17),
                BigDecimal.valueOf(-75.00),
                "T5678 17/04",
                "Transport",
                "Uber Ride",
                BigDecimal.valueOf(2625),
                SourceType.MANUAL,
                mockAccount,
                validUser
        ));

    }

    @BeforeEach
    void setUp() {
    }

    @Test
    void addTransaction_shouldCreateTransactionAndUpdateAccountBalance() {
        // Arrange
        TransactionRequest request = new TransactionRequest(
                LocalDate.of(2025, 4, 15),
                BigDecimal.valueOf(12.5),
                "V3344 10/04",
                "Restaurants",
                DEBIT,
                "McDonald's",
                BigDecimal.valueOf(143.17),
                SourceType.MANUAL);

        User mockUser = validUser;
        Account mockAccount = new Account();
        mockAccount.setBalance(BigDecimal.ZERO);

        BigDecimal expectedAmount = request.getType() == DEBIT
                ? request.getAmount().multiply( BigDecimal.valueOf(-1))
                : request.getAmount();

        when(userRepo.getUserByEmail(validUserEmail)).thenReturn(Optional.of(mockUser));
        when(accountRepo.findAccountByUserAndType(mockUser, SPENDING)).thenReturn(Optional.of(mockAccount));
        when(transactionRepo.save(any(Transaction.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Transaction txn = transactionService.addTransaction(validUserEmail, request);

        // Assert
        assertEquals(expectedAmount, txn.getAmount());
        verify(accountService).updateAccountBalance(mockAccount, txn.getBalanceAtTransaction());
    }

    @Test
    void addTransaction_shouldThrowIfUserNotFound() {
        when(userRepo.getUserByEmail(invalidUserEmail)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
                transactionService.addTransaction(invalidUserEmail, new TransactionRequest()));
    }

    @Test
    void addTransaction_shouldCreateNewSpendingAccountIfNotFound() {
        // Arrange
        BigDecimal mockTransactionBalance = BigDecimal.TEN;
        Account mockAccount = new Account();
        mockAccount.setBalance(mockTransactionBalance);

        TransactionRequest request = new TransactionRequest(
                LocalDate.now(),
                BigDecimal.valueOf(20),
                "Test Desc",
                "Test Category",
                DEBIT,
                "Test Merchant",
                mockTransactionBalance,
                SourceType.MANUAL
        );

        when(userRepo.getUserByEmail(validUserEmail)).thenReturn(Optional.of(validUser));
        when(accountRepo.findAccountByUserAndType(validUser, SPENDING)).thenReturn(Optional.empty());
        when(accountService.createSpendingAccount(validUser, mockTransactionBalance)).thenReturn(mockAccount);
        when(transactionRepo.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Transaction txn = transactionService.addTransaction(validUserEmail, request);

        //Assert
        // Assert
        assertEquals(mockAccount, txn.getAccount());
        verify(accountService).createSpendingAccount(validUser, mockTransactionBalance);
    }

    @Test
    void getAllTransactionsByUserId_shouldReturnAllTransactions() {
        // Arrange
        List<Transaction> mockTransactions = Arrays.asList(
                transactionMap.get("Transaction1"),
                transactionMap.get("Transaction2"),
                transactionMap.get("Transaction3")
        );
        when(userRepo.existsById(validUser.getId())).thenReturn(true);
        when(transactionRepo.findByUser_Id(validUser.getId())).thenReturn(mockTransactions);

        // Act
        List<Transaction> result = transactionService.getAllTransactionsByUserId(validUser.getId());

        // Assert
        assertEquals(mockTransactions, result);
    }

    @Test
    void getAllTransactionsByUserId_shouldThrowIfUserNotFound() {
        when(userRepo.existsById(invalidUserId)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () ->
                transactionService.getAllTransactionsByUserId(invalidUserId));
    }

    @Test
    void getTransactionsByUserIdInTimeFrame_shouldReturnTransactions() {
        // Arrange
        List<Transaction> mockTransactions = Arrays.asList(
                transactionMap.get("Transaction1"),
                transactionMap.get("Transaction2"),
                transactionMap.get("Transaction3")
        );
        LocalDate startDate = LocalDate.of(2025, 4, 15);
        LocalDate endDate = LocalDate.of(2025, 4, 16);
        when(userRepo.existsById(validUser.getId())).thenReturn(true);
        when(transactionRepo.findByUserIdAndDateBetween(validUser.getId(), startDate, endDate)).thenReturn(mockTransactions);

        // Act
        List<Transaction> result = transactionService.getTransactionsByUserIdInTimeFrame(validUser.getId(), startDate, endDate);

        //Assert
        assertEquals(mockTransactions, result);
    }

    @Test
    void getTransactionsByUserIdInTimeFrame_shouldThrowIfUserNotFound() {
        LocalDate startDate = LocalDate.of(2025, 4, 15);
        LocalDate endDate = LocalDate.of(2025, 4, 16);

        when(userRepo.existsById(invalidUserId)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () ->
                transactionService.getTransactionsByUserIdInTimeFrame(invalidUserId, startDate, endDate));
    }

    @Test
    void getTransactionsByUserIdInTimeFrame_shouldThrowIfStartDateNull() {
        LocalDate startDate = null;
        LocalDate endDate = LocalDate.of(2025, 4, 16);

        when(userRepo.existsById(validUser.getId())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () ->
                transactionService.getTransactionsByUserIdInTimeFrame(validUser.getId(), startDate, endDate));
    }

    @Test
    void getTransactionsByUserIdInTimeFrame_shouldThrowIfEndDateNull() {
        LocalDate startDate = LocalDate.of(2025, 4, 15);
        LocalDate endDate = null;

        when(userRepo.existsById(validUser.getId())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () ->
                transactionService.getTransactionsByUserIdInTimeFrame(validUser.getId(), startDate, endDate));
    }

    @Test
    void getTransactionsByUserIdInTimeFrame_shouldThrowIfInvalidDateRange() {
        LocalDate startDate = LocalDate.of(2025, 4, 16);
        LocalDate endDate = LocalDate.of(2025, 4, 15);

        when(userRepo.existsById(validUser.getId())).thenReturn(true);

        assertThrows(InvalidDateRangeException.class, () ->
                transactionService.getTransactionsByUserIdInTimeFrame(validUser.getId(), startDate, endDate));
    }

    @Test
    void getTransactionsByUserIdInTimeFrame_shouldThrowIfUserIdNotFound() {
        LocalDate startDate = LocalDate.of(2025, 4, 15);
        LocalDate endDate = LocalDate.of(2025, 4, 16);
        when(userRepo.existsById(invalidUserId)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () ->
                transactionService.getTransactionsByUserIdInTimeFrame(invalidUserId, startDate, endDate));
    }

    @Test
    void getTransactionsByUserIdInTimeFrame_shouldThrowIfStartDateNotFound() {
        LocalDate startDate = null;
        LocalDate endDate = LocalDate.of(2025, 4, 16);

        when(userRepo.existsById(validUser.getId())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () ->
                transactionService.getTransactionsByUserIdInTimeFrame(validUser.getId(), startDate, endDate));
    }

    @Test
    void getTransactionsByUserIdPaginated_shouldReturnTransactions() {
        // Arrange
        int page = 0;
        int size = 3;
        List<Transaction> mockTransactions = Arrays.asList(
                transactionMap.get("Transaction1"),
                transactionMap.get("Transaction2"),
                transactionMap.get("Transaction3")
        );
        Page<Transaction> mockPage = new PageImpl<>(
                mockTransactions,
                PageRequest.of(page, size),
                mockTransactions.size()
        );

        when(userRepo.existsById(validUser.getId())).thenReturn(true);
        when(transactionRepo.findByUser_Id(validUser.getId(), PageRequest.of(page, size))).thenReturn(mockPage);

        // Act
        Page<Transaction>  result = transactionService.getTransactionsByUserIdPaginated(validUser.getId(), page, size);
        // Assert
        assertEquals(mockPage, result);
    }

    @Test
    void getTransactionsByUserIdPaginated_shouldThrowIfUserIdNotFound() {
        int page = 0;
        int size = 3;
        when(userRepo.existsById(invalidUserId)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () ->
                transactionService.getTransactionsByUserIdPaginated(invalidUserId, page, size));
    }

    @Test
    void getTransactionsByUserIdPaginated_shouldThrowIfPageLessThanZero() {
        int page = -1;
        int size = 3;
        when(userRepo.existsById(validUser.getId())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () ->
                transactionService.getTransactionsByUserIdPaginated(validUser.getId(), page, size));
    }

    @Test
    void getTransactionsByUserIdPaginated_shouldThrowIfSizeLessThanZero() {
        int page = 0;
        int size = -1;
        when(userRepo.existsById(validUser.getId())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () ->
                transactionService.getTransactionsByUserIdPaginated(validUser.getId(), page, size));
    }

    @Test
    void getAllTransactionsByUserIdSortedOldest_shouldReturnTransactions() {
        // Arrange
        List<Transaction> mockTransactions = Arrays.asList(
                transactionMap.get("Transaction1"),
                transactionMap.get("Transaction2"),
                transactionMap.get("Transaction3")
        );

        when(userRepo.existsById(validUser.getId())).thenReturn(true);
        when(transactionRepo.findByUser_IdOrderByDateAsc(validUser.getId())).thenReturn(mockTransactions);

        // Act
        List<Transaction>  result = transactionService.getAllTransactionsByUserIdSortedOldest(validUser.getId());
        // Assert
        assertEquals(mockTransactions, result);
    }

    @Test
    void getAllTransactionsByUserIdSortedOldest_shouldThrowIfUserIdNotFound() {
        when(userRepo.existsById(invalidUserId)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () ->
                transactionService.getAllTransactionsByUserIdSortedOldest(invalidUserId));
    }

    @Test
    void getTransactionsByUserIdSortedNewest_shouldReturnTransactions() {
        // Arrange
        List<Transaction> mockTransactions = Arrays.asList(
                transactionMap.get("Transaction1"),
                transactionMap.get("Transaction2"),
                transactionMap.get("Transaction3")
        );

        when(userRepo.existsById(validUser.getId())).thenReturn(true);
        when(transactionRepo.findByUser_IdOrderByDateDesc(validUser.getId())).thenReturn(mockTransactions);

        // Act
        List<Transaction>  result = transactionService.getTransactionsByUserIdSortedNewest(validUser.getId());
        // Assert
        assertEquals(mockTransactions, result);
    }

    @Test
    void getTransactionsByUserIdSortedNewest_shouldThrowIfUserIdNotFound() {
        when(userRepo.existsById(invalidUserId)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () ->
                transactionService.getTransactionsByUserIdSortedNewest(invalidUserId));
    }

    @Test
    void getAllTransactionsByUserIdTimeFrame_shouldReturnTransactions() {
        // Arrange
        String timeFrame = "7days";
        LocalDate startDate = LocalDate.now().minusDays(7);

        List<Transaction> mockTransactions = Arrays.asList(
                transactionMap.get("Transaction1"),
                transactionMap.get("Transaction2"),
                transactionMap.get("Transaction3")
        );

        when(userRepo.existsById(validUser.getId())).thenReturn(true);
        when(transactionRepo.findByUserIdAndDateBetween(validUser.getId(), startDate, LocalDate.now())).thenReturn(mockTransactions);

        // Act
        List<Transaction>  result = transactionService.getAllTransactionsByUserIdTimeFrame(validUser.getId(), timeFrame);
        // Assert
        assertEquals(mockTransactions, result);
    }

    @Test
    void getAllTransactionsByUserIdTimeFrame_shouldThrowIfUserIdNotFound() {
        String timeFrame = "7days";
        when(userRepo.existsById(invalidUserId)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () ->
                transactionService.getAllTransactionsByUserIdTimeFrame(invalidUserId, timeFrame));
    }

    @Test
    void getTransactionSummaryByTimeFrame_shouldReturnTransactionSummaryResponse() {
        // Arrange
        String timeFrame = "7days";

        List<Transaction> mockTransactions = Arrays.asList(
                transactionMap.get("Transaction1"),
                transactionMap.get("Transaction2"),
                transactionMap.get("Transaction3")
        );
        LocalDate earliestDate = mockTransactions.get(0).getDate();
        LocalDate latestDate = mockTransactions.get(2).getDate();

        when(userRepo.existsById(validUser.getId())).thenReturn(true);
        when(transactionRepo.findByUserIdAndDateBetween(anyLong(), any(), any())).thenReturn(mockTransactions);

        // Act
        TransactionSummaryResponse  result = transactionService.getTransactionSummaryByTimeFrame(validUser.getId(), timeFrame);

        // Assert
        assertEquals(mockTransactions.size(), result.getCount());
        assertEquals(earliestDate, result.getEarliest());
        assertEquals(latestDate, result.getLatest());
    }

    @Test
    void getTransactionSummaryByTimeFrame_shouldThrowIfUserIdNotFound() {
        String timeFrame = "7days";
        when(userRepo.existsById(invalidUserId)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () ->
                transactionService.getTransactionSummaryByTimeFrame(invalidUserId, timeFrame));
    }

    @Test
    void getTransactionSummaryByTimeFrame_returnsEmptySummary_whenNoTransactions() {
        String timeFrame = "7days";
        when(userRepo.existsById(validUser.getId())).thenReturn(true);
        when(transactionRepo.findByUserIdAndDateBetween(anyLong(), any(), any())).thenReturn(null);

        // Act
        TransactionSummaryResponse  result = transactionService.getTransactionSummaryByTimeFrame(validUser.getId(), timeFrame);

        // Assert
        assertNull(result.getCount());
        assertNull(result.getEarliest());
        assertNull(result.getLatest());
    }

    @Test
    void getCurrentBalanceByUser_shouldReturnSpendingAccountBalance() {
        // Arrange
        Account mockAccount = new Account();
        BigDecimal balance = new BigDecimal(1000);
        mockAccount.setBalance(balance);

        when(userRepo.getUserByEmail(validUserEmail)).thenReturn(Optional.of(validUser));
        when(accountRepo.findAccountByUserAndType(validUser, SPENDING)).thenReturn(Optional.of(mockAccount));

        // Act
        BigDecimal result = transactionService.getCurrentBalanceByUser(validUserEmail);

        // Assert
        assertEquals(mockAccount.getBalance(), result);
    }

    @Test
    void getCurrentBalanceByUser_shouldThrowIfUserNotFound() {
        when(userRepo.getUserByEmail(invalidUserEmail)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
                transactionService.getCurrentBalanceByUser(invalidUserEmail));
    }

    @Test
    void getCurrentBalanceByUser_createSpendingAccountIfNotFound() {
        // Arrange
        Account mockAccount = new Account();
        mockAccount.setBalance(BigDecimal.ZERO);

        when(userRepo.getUserByEmail(validUserEmail)).thenReturn(Optional.of(validUser));
        when(accountRepo.findAccountByUserAndType(validUser, SPENDING)).thenReturn(Optional.empty());
        when(accountService.createSpendingAccount(validUser, BigDecimal.ZERO)).thenReturn(mockAccount);

        // Act
        BigDecimal resultBalance = transactionService.getCurrentBalanceByUser(validUserEmail);

        //Assert
        assertEquals(mockAccount.getBalance(), resultBalance);
        verify(accountService).createSpendingAccount(validUser, BigDecimal.ZERO);
    }

    @Test
    void updateTransaction_modifyTransactionAndUpdateAccountBalance() {
        // Arrange
        Transaction mockTransaction = new Transaction(transactionMap.get("Transaction1"));
        TransactionRequest request = new TransactionRequest(
                LocalDate.of(2025, 4, 15),
                BigDecimal.valueOf(12.5),
                "V3344 10/04",
                "Restaurants",
                DEBIT,
                "McDonald's",
                BigDecimal.valueOf(143.17),
                SourceType.MANUAL);

        Account mockAccount = new Account();
        mockAccount.setBalance(request.getBalanceAtTransaction());

        BigDecimal expectedAmount = request.getType() == DEBIT
                ? request.getAmount().multiply( BigDecimal.valueOf(-1))
                : request.getAmount();

        when(userRepo.getUserByEmail(validUserEmail)).thenReturn(Optional.of(validUser));
        when(transactionRepo.findTransactionByIdAndUser(validUser, mockTransaction.getId())).thenReturn(Optional.of(mockTransaction));
        when(accountService.syncSpendingAccountBalance(mockTransaction, expectedAmount)).thenReturn(mockAccount);
        when(transactionRepo.save(any(Transaction.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Transaction txn = transactionService.updateTransaction(validUserEmail, mockTransaction.getId(), request);

        // Assert
        assertEquals(request.getDate(), txn.getDate());
        assertEquals(expectedAmount, txn.getAmount());
        assertEquals(request.getMerchant(), txn.getMerchant());
        assertEquals(request.getDescription(), txn.getDescription());
        assertEquals(request.getBalanceAtTransaction(), txn.getBalanceAtTransaction());

        verify(accountService).syncSpendingAccountBalance(mockTransaction, expectedAmount);
        verify(transactionRepo).save(mockTransaction);
    }

    @Test
    void updateTransaction_shouldThrowIfUserNotFound() {
        TransactionRequest mockRequest = new TransactionRequest();
        Transaction mockTransaction = new Transaction(transactionMap.get("Transaction1"));
        long mockTransactionId = mockTransaction.getId();
        when(userRepo.getUserByEmail(invalidUserEmail)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
                transactionService.updateTransaction(invalidUserEmail, mockTransactionId, mockRequest));
    }

    @Test
    void updateTransaction_shouldThrowIfTransactionNotFound() {
        TransactionRequest mockRequest = new TransactionRequest();
        Transaction mockTransaction = new Transaction(transactionMap.get("Transaction1"));
        long mockTransactionId = mockTransaction.getId();
        when(userRepo.getUserByEmail(validUserEmail)).thenReturn(Optional.of(validUser));
        when(transactionRepo.findTransactionByIdAndUser(validUser, mockTransactionId)).thenReturn(Optional.empty());

        assertThrows(TransactionNotFoundException.class, () ->
                transactionService.updateTransaction(validUserEmail, mockTransactionId, mockRequest));
    }

    @Test
    void deleteTransaction_shouldDeleteTransactionAndRemoveAccountBalance() {
        // Arrange
        Transaction mockTransaction = new Transaction(transactionMap.get("Transaction1"));
        BigDecimal mockStartingBalance = BigDecimal.valueOf(100);
        Account mockAccount = new Account();
        mockAccount.setBalance(mockStartingBalance);
        mockTransaction.setAccount(mockAccount);

        when(userRepo.getUserByEmail(validUserEmail)).thenReturn(Optional.of(validUser));
        when(transactionRepo.findTransactionByIdAndUser(validUser, mockTransaction.getId())).thenReturn(Optional.of(mockTransaction));;
        when(accountRepo.save(any(Account.class))).thenAnswer(i -> i.getArgument(0));
        // Act
        transactionService.deleteTransaction(validUserEmail, mockTransaction.getId());

        // Assert

        assertEquals(mockStartingBalance.subtract(mockTransaction.getAmount()), mockAccount.getBalance());
        verify(transactionRepo).delete(mockTransaction);
    }

    @Test
    void deleteTransaction_shouldThrowIfUserNotFound() {
        Transaction mockTransaction = new Transaction(transactionMap.get("Transaction1"));
        long mockTransactionId = mockTransaction.getId();
        when(userRepo.getUserByEmail(invalidUserEmail)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
                transactionService.deleteTransaction(invalidUserEmail, mockTransactionId));
    }

    @Test
    void deleteTransaction_shouldThrowIfTransactionNotFound() {
        Transaction mockTransaction = new Transaction();
        long mockTransactionId = mockTransaction.getId();
        when(userRepo.getUserByEmail(validUserEmail)).thenReturn(Optional.of(validUser));
        when(transactionRepo.findTransactionByIdAndUser(validUser, mockTransactionId)).thenReturn(Optional.empty());

        assertThrows(TransactionNotFoundException.class, () ->
                transactionService.deleteTransaction(validUserEmail, mockTransactionId));
    }

    @Test
    void deleteTransactionsById_DeleteAllTransactions() {
        // Arrange
        List<Long> mocktransactionIdList = transactionMap.values().stream()
                .map(Transaction::getId)
                .toList();

        Account mockAccount = new Account();

        when(userRepo.getUserByEmail(validUserEmail)).thenReturn(Optional.of(validUser));
        when(accountRepo.findAccountByUserAndType(validUser, SPENDING)).thenReturn(Optional.of(mockAccount));

        // Act
        transactionService.deleteTransactionsById(validUserEmail, mocktransactionIdList);

        // Assert
        verify(transactionRepo).deleteAllByIdInAndUser(mocktransactionIdList, validUser);
        verify(accountService).recalculateBalanceForSpendingAccount(mockAccount);
    }

    @Test
    void deleteTransactionsById_shouldThrowIfUserNotFound() {
        List<Long> mocktransactionIdList = transactionMap.values().stream()
                .map(Transaction::getId)
                .toList();
        when(userRepo.getUserByEmail(invalidUserEmail)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
                transactionService.deleteTransactionsById(invalidUserEmail, mocktransactionIdList));
    }

    @Test
    void deleteTransactionsById_shouldThrowIfAccountNotFound() {
        List<Long> mocktransactionIdList = transactionMap.values().stream()
                .map(Transaction::getId)
                .toList();
        when(userRepo.getUserByEmail(validUserEmail)).thenReturn(Optional.of(validUser));
        when(accountRepo.findAccountByUserAndType(validUser, SPENDING)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () ->
                transactionService.deleteTransactionsById(validUserEmail, mocktransactionIdList));
    }

    @Test
    void importMultipleCSVs_shouldCreateTransactionsFromRows() throws IOException {
        // Arrange
        List<Transaction> expectedTransactions = Arrays.asList(
                transactionMap.get("Transaction1"),
                transactionMap.get("Transaction2"),
                transactionMap.get("Transaction3"));
        ClassPathResource resource = new ClassPathResource(VALID_SAMPLE.getPath());
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "sample1.csv",
                "text/csv",
                resource.getInputStream()
        );

        when(userRepo.findById(validUser.getId())).thenReturn(Optional.of(validUser));
        when(accountService.handleAccountNoAssign(anyInt(), eq(validUser))).thenReturn(mockAccount);
        when(accountRepo.findAccountByUserAndType(validUser, SPENDING)).thenReturn(Optional.of(mockAccount));

        // Act
        List<Transaction> importedTransactions = transactionService.importMultipleCSVs(validUser.getId(), file);

        // Assert
        assertEquals(expectedTransactions.size(), importedTransactions.size());

        TestUtils.assertListElementsMatch(expectedTransactions, importedTransactions, (expected, actual) -> {
            assertEquals(expected.getDate(), actual.getDate());
            assertEquals(0, expected.getAmount().compareTo(actual.getAmount()));
            assertEquals(expected.getAccount(), actual.getAccount());
            assertEquals(expected.getDescription(), actual.getDescription());
            assertEquals(0, expected.getBalanceAtTransaction().compareTo(actual.getBalanceAtTransaction()));
            assertEquals(expected.getCategory(), actual.getCategory());
            assertEquals(expected.getMerchant(), actual.getMerchant());
            assertEquals(expected.getUser(), actual.getUser());
        });

        verify(transactionRepo).saveAll(anyList());
        verify(accountService).recalculateBalanceForSpendingAccount(mockAccount);
    }

    @Test
    void importMultipleCSVs_shouldThrowIfUserNotFound() throws IOException {
        ClassPathResource resource = new ClassPathResource("");
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "sample2.csv",
                "text/csv",
                resource.getInputStream()
        );
        when(userRepo.findById(invalidUserId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
                transactionService.importMultipleCSVs(invalidUserId, file));
    }

    @Test
    void importMultipleCSVs_shouldThrowIfAccountNotFound() throws IOException {
        ClassPathResource resource = new ClassPathResource(VALID_SAMPLE.getPath());
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "sample1.csv",
                "text/csv",
                resource.getInputStream()
        );

        when(userRepo.findById(validUser.getId())).thenReturn(Optional.of(validUser));
        when(accountService.handleAccountNoAssign(anyInt(), eq(validUser))).thenReturn(mockAccount);
        when(accountRepo.findAccountByUserAndType(validUser, SPENDING)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () ->
                transactionService.importMultipleCSVs(validUser.getId(), file));
    }

    @Test
    void importMultipleCSVs_shouldThrowIfFileIsNull() {
        assertThrows(IllegalArgumentException.class, () ->
                transactionService.importMultipleCSVs(validUser.getId(), null));
    }
    @Test
    void importMultipleCSVs_shouldThrowIfFileIsEmpty() {
        MockMultipartFile emptyFile = new MockMultipartFile(
                "file", "empty.csv", "text/csv", new byte[0]
        );

        assertThrows(IllegalArgumentException.class, () ->
                transactionService.importMultipleCSVs(validUser.getId(), emptyFile));
    }

    @Test
    void importMultipleCSVs_shouldThrowIfFileIsNotCSV() {
        MockMultipartFile badTypeFile = new MockMultipartFile(
                "file", "sample.txt", "text/plain", "some text".getBytes()
        );

        assertThrows(IllegalArgumentException.class, () ->
                transactionService.importMultipleCSVs(validUser.getId(), badTypeFile));
    }

    @Test
    void importMultipleCSVs_shouldThrowIfFileHasWrongExtension() {
        MockMultipartFile wrongExt = new MockMultipartFile(
                "file", "data.txt", "text/csv", "fake,csv,data".getBytes()
        );

        assertThrows(IllegalArgumentException.class, () ->
                transactionService.importMultipleCSVs(validUser.getId(), wrongExt));
    }

}
