package com.shoxys.budgetbuddy_backend.Services;

import com.shoxys.budgetbuddy_backend.DTOs.TransactionRequest;
import com.shoxys.budgetbuddy_backend.Entities.Account;
import com.shoxys.budgetbuddy_backend.Entities.Transaction;
import com.shoxys.budgetbuddy_backend.Entities.User;
import com.shoxys.budgetbuddy_backend.Enums.AccountType;
import com.shoxys.budgetbuddy_backend.Enums.BankCsvColumn;
import com.shoxys.budgetbuddy_backend.Enums.SourceType;
import com.shoxys.budgetbuddy_backend.Enums.TransactionType;
import com.shoxys.budgetbuddy_backend.Exceptions.AccountNotFoundException;
import com.shoxys.budgetbuddy_backend.Exceptions.InvalidDateRangeException;
import com.shoxys.budgetbuddy_backend.Exceptions.TransactionNotFoundException;
import com.shoxys.budgetbuddy_backend.Exceptions.UserNotFoundException;
import com.shoxys.budgetbuddy_backend.Repo.AccountRepo;
import com.shoxys.budgetbuddy_backend.Repo.TransactionRepo;
import com.shoxys.budgetbuddy_backend.Repo.UserRepo;
import com.shoxys.budgetbuddy_backend.Utils;
import com.shoxys.budgetbuddy_backend.DTOs.TransactionSummaryResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.shoxys.budgetbuddy_backend.Enums.AccountType.SPENDING;
import static com.shoxys.budgetbuddy_backend.Enums.BankCsvColumn.*;
import static com.shoxys.budgetbuddy_backend.Enums.TransactionType.DEBIT;

@Service
public class TransactionService {
    @Autowired
    private TransactionRepo transactionRepo;
    @Autowired
    private AccountRepo accountRepo;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private AccountService accountService;

    public List<Transaction> getAllTransactionsByUserId(long userId) {
        if (!userRepo.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }
        return transactionRepo.findByUser_Id(userId);
    }

    public List<Transaction> getTransactionsByUserIdInTimeFrame(Long userId, LocalDate startDate, LocalDate endDate) {
        if (!userRepo.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }
        if (startDate == null) {
            throw new IllegalArgumentException("startDate must be valid");
        }
        if (endDate == null) {
            throw new IllegalArgumentException("endDate must be valid");
        }

        if (startDate.isAfter(endDate)) {
            throw new InvalidDateRangeException(startDate, endDate);
        }

        return transactionRepo.findByUserIdAndDateBetween(userId, startDate, endDate);
    }

    public Page<Transaction> getTransactionsByUserIdPaginated(Long userId, int page, int size) {
        if (!userRepo.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }
        if (page < 0) {
            throw new IllegalArgumentException("page must be greater than or equal to 0");
        }
        if (size < 0) {
            throw new IllegalArgumentException("size must be greater than or equal to 0");
        }
        return transactionRepo.findByUser_Id(userId, PageRequest.of(page, size));
    }

    public List<Transaction> getAllTransactionsByUserIdSortedOldest(Long userId) {
        if (!userRepo.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }
        return transactionRepo.findByUser_IdOrderByDateAsc(userId);
    }

    public List<Transaction> getTransactionsByUserIdSortedNewest(Long userId) {
        if (!userRepo.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }
        return transactionRepo.findByUser_IdOrderByDateDesc(userId);
    }

    public List<Transaction> getAllTransactionsByUserIdTimeFrame(Long userId, String timeFrame) {
        if (!userRepo.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }
        if  (Utils.nullOrEmpty(timeFrame)) {
            throw new IllegalArgumentException("timeFrame must be valid");
        }
        LocalDate startDate = switch (timeFrame.toLowerCase()) {
            case "7days" -> LocalDate.now().minusDays(7);
            case "30days" -> LocalDate.now().minusDays(30);
            case "60days" -> LocalDate.now().minusDays(60);
            case "90days" -> LocalDate.now().minusDays(90);
            default -> LocalDate.MIN; // "all time"
        };
        return transactionRepo.findByUserIdAndDateBetween(userId, startDate, LocalDate.now());
    }

    public TransactionSummaryResponse getTransactionSummaryByTimeFrame(Long userId, String timeFrame) {
        if (!userRepo.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }

        // Get list of transactions for user by time frame
        List<Transaction> transactions = getAllTransactionsByUserIdTimeFrame(userId, timeFrame);

        if (Utils.nullOrEmpty(transactions)) {
            return new TransactionSummaryResponse(null, null, null);
        }

        LocalDate earliest = transactions.stream()
                .map(Transaction::getDate)
                .min(LocalDate::compareTo)
                .orElse(null);

        LocalDate latest = transactions.stream()
                .map(Transaction::getDate)
                .max(LocalDate::compareTo)
                .orElse(null);

        return new TransactionSummaryResponse(transactions.size(), earliest, latest);
    }

    public BigDecimal getCurrentBalanceByUser(String email){
        User user = userRepo.getUserByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));

        // Find associated spending account else create new account with initial balance as 0
        Account spendingAccount = accountRepo.findAccountByUserAndType(user, SPENDING)
                .orElseGet(()-> accountService.createSpendingAccount(user, BigDecimal.ZERO));

        return spendingAccount.getBalance();
    }

    @Transactional
    public Transaction addTransaction(String email, TransactionRequest request) {
        User user = userRepo.getUserByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));

        // Find associated spending account else create new account
        Account spendingAccount = accountRepo.findAccountByUserAndType(user, SPENDING)
                .orElseGet(()-> accountService.createSpendingAccount(user, request.getBalanceAtTransaction()));

        TransactionType type = request.getType();
        // Change to negative values if type DEBIT
        BigDecimal amount = type == DEBIT ? request.getAmount().multiply(BigDecimal.valueOf(-1)) : request.getAmount();

        // Update balance
        BigDecimal newBalance = spendingAccount.getBalance().add(amount);

        // Update account balance
        accountService.updateAccountBalance(spendingAccount, newBalance);

        Transaction newTransaction = new Transaction(
                request.getDate(),
                amount,
                request.getDescription(),
                request.getCategory(),
                request.getMerchant(),
                newBalance,
                SourceType.MANUAL,
                spendingAccount,
                user
        );
        return transactionRepo.save(newTransaction);
    }

    @Transactional
    public Transaction updateTransaction(String email, long id, TransactionRequest request) {
        User user = userRepo.getUserByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));

        Transaction transaction = transactionRepo.findTransactionByIdAndUser(user, id)
                .orElseThrow(() -> new TransactionNotFoundException(id));

        // Calculate new amount (DEBIT = negative)
        TransactionType type = request.getType();
        BigDecimal newAmount = type == DEBIT
                ? request.getAmount().multiply(BigDecimal.valueOf(-1))
                : request.getAmount();

        // Sync balance after transaction update ties to transaction
        Account account = accountService.syncSpendingAccountBalance(transaction, newAmount);

        // Update transaction fields
        transaction.setDate(request.getDate());
        transaction.setAmount(newAmount);
        transaction.setDescription(request.getDescription());
        transaction.setCategory(request.getCategory());
        transaction.setMerchant(request.getMerchant());
        transaction.setBalanceAtTransaction(account.getBalance());

        return transactionRepo.save(transaction);
    }

    @Transactional
    public void deleteTransaction(String email, Long id) {
        User user = userRepo.getUserByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));

        Transaction txn = transactionRepo.findTransactionByIdAndUser(user, id)
                .orElseThrow(() -> new TransactionNotFoundException(id));

        Account acc = txn.getAccount();
        acc.setBalance(acc.getBalance().subtract(txn.getAmount()));
        accountRepo.save(acc);
        transactionRepo.delete(txn);;
    }

    @Transactional
    public void deleteTransactionsById(String email, List<Long> ids){
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("ids must not be null or empty");
        }
        User user = userRepo.getUserByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));

        // Associated spending account
        Account account = accountRepo.findAccountByUserAndType(user, SPENDING)
                .orElseThrow(() -> new AccountNotFoundException(user));
        transactionRepo.deleteAllByIdInAndUser(ids, user);
        // Recalculate account balance after deletion
        accountService.recalculateBalanceForSpendingAccount(account);
    }

    @Transactional
    public List<Transaction> importMultipleCSVs(long userId, MultipartFile file){
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File must not be empty");
        }

        // Check content type
        if (!"text/csv".equalsIgnoreCase(file.getContentType())) {
            throw new IllegalArgumentException("File must be a CSV");
        }

        // Check file extension
        if (!file.getOriginalFilename().toLowerCase().endsWith(".csv")) {
            throw new IllegalArgumentException("File must have a .csv extension");
        }

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String headerLine = reader.readLine();
            String[] headers = headerLine.split(",");

            for (int i = 0; i < headers.length; i++) {
                if (headers[i].trim().isEmpty()) {
                    headers[i] = UNUSED.getStr() + i;  // or whatever name you want
                }
            }

            CSVFormat format = CSVFormat.DEFAULT
                    .withHeader(headers);

            CSVParser csvParser = new CSVParser(reader, format);

            List<Transaction> transactions = new ArrayList<>();

            for (CSVRecord record : csvParser) {
                // Parse and validate fields safely by header name or index
                int accountNo;
                try {
                    accountNo = Integer.parseInt(record.get(ACCOUNT_NUMBER.getStr()).trim());
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Account number must be an integer");
                }

                Account account = accountService.handleAccountNoAssign(accountNo, user);

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMM yy", Locale.ENGLISH);
                LocalDate date = LocalDate.parse(record.get(DATE.getStr()), formatter);

                String merchant = record.isSet(MERCHANT.getStr()) && !record.get(MERCHANT.getStr()).isEmpty()
                        ? record.get(MERCHANT.getStr()).trim()
                        : null;

                Transaction txn = new Transaction();
                txn.setDate(date);
                txn.setAmount(new BigDecimal(record.get(AMOUNT.getStr())));
                txn.setAccount(account);
                txn.setDescription(record.get(DETAILS.getStr()).trim());
                txn.setBalanceAtTransaction(new BigDecimal(record.get(BALANCE.getStr())));
                txn.setCategory(record.get(CATEGORY.getStr()).trim());
                txn.setMerchant(merchant);
                txn.setUser(user);

                transactions.add(txn);
            }
            // Associated spending account
            Account account = accountRepo.findAccountByUserAndType(user, SPENDING)
                    .orElseThrow(() -> new AccountNotFoundException(user));
            // Save all transactions to database
            transactionRepo.saveAll(transactions);
            // Recalculate account balance
            accountService.recalculateBalanceForSpendingAccount(account);

            return transactions;

        } catch (IOException e) {
            throw new RuntimeException("Failed to read CSV file", e);
        }
    }
}
