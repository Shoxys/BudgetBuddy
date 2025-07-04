package com.shoxys.budgetbuddy_backend.Services;

import com.shoxys.budgetbuddy_backend.DTOs.TransactionRequest;
import com.shoxys.budgetbuddy_backend.Entities.Account;
import com.shoxys.budgetbuddy_backend.Entities.Transaction;
import com.shoxys.budgetbuddy_backend.Entities.User;
import com.shoxys.budgetbuddy_backend.Enums.AccountType;
import com.shoxys.budgetbuddy_backend.Enums.SourceType;
import com.shoxys.budgetbuddy_backend.Repo.AccountRepo;
import com.shoxys.budgetbuddy_backend.Repo.TransactionRepo;
import com.shoxys.budgetbuddy_backend.Repo.UserRepo;
import com.shoxys.budgetbuddy_backend.Utils;
import com.shoxys.budgetbuddy_backend.DTOs.TransactionSummaryResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
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
import java.util.ArrayList;
import java.util.List;

@Service
public class TransactionService {
    @Autowired
    private TransactionRepo transactionRepo;
    private AccountRepo accountRepo;
    private UserRepo userRepo;

    public List<Transaction> getAllTransactionsByUserId(long userId) {
        if (!userRepo.existsById(userId)) {
            throw new EntityNotFoundException("User with id " + userId + " not found");
        }
        return transactionRepo.findByUser_Id(userId);
    }

    public List<Transaction> getTransactionsByUserIdInTimeFrame(Long userId, LocalDate startDate, LocalDate endDate) {
        if (!userRepo.existsById(userId)) {
            throw new EntityNotFoundException("User with id " + userId + " not found");
        }
        if (startDate == null) {
            throw new IllegalArgumentException("startDate must be valid");
        }
        if (endDate == null) {
            throw new IllegalArgumentException("endDate must be valid");
        }
        return transactionRepo.findByUserIdAndDateBetween(userId, startDate, endDate);
    }

    public Page<Transaction> getTransactionsByUserIdPaginated(Long userId, int page, int size) {
        if (!userRepo.existsById(userId)) {
            throw new EntityNotFoundException("User with id " + userId + " not found");
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
            throw new EntityNotFoundException("User with id " + userId + " not found");
        }
        return transactionRepo.findByUser_IdOrderByDateAsc(userId);
    }

    public List<Transaction> getTransactionsByUserIdSortedNewest(Long userId) {
        if (!userRepo.existsById(userId)) {
            throw new EntityNotFoundException("User with id " + userId + " not found");
        }
        return transactionRepo.findByUser_IdOrderByDateDesc(userId);
    }

    public List<Transaction> getAllTransactionsByUserIdTimeFrame(Long userId, String timeFrame) {
        if (!userRepo.existsById(userId)) {
            throw new EntityNotFoundException("User with id " + userId + " not found");
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
        List<Transaction> transactions = getAllTransactionsByUserIdTimeFrame(userId, timeFrame);

        if (transactions.isEmpty()) {
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

    @Transactional
    public Transaction addTransaction(String email, TransactionRequest request) {
        User user = userRepo.getUserByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Account spendingAccount = accountRepo.findAccountByUserAndType(user, AccountType.SPENDING)
                .orElseGet(()-> {
                    Account newAccount = new Account("Spending Account", AccountType.SPENDING, null, request.getBalanceAtTransaction(), true, user);
                    return accountRepo.save(newAccount);
                });

        Transaction newTransaction = new Transaction(
                request.getDate(),
                request.getAmount(),
                request.getDescription(),
                request.getCategory(),
                request.getMerchant(),
                request.getBalanceAtTransaction(),
                SourceType.MANUAL,
                spendingAccount,
                user
        );
        return transactionRepo.save(newTransaction);
    }

    @Transactional
    public Transaction updateTransaction(Long id, TransactionRequest request) {
        Transaction transaction = transactionRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Transaction with id " + id + " not found"));

        transaction.setId(id); // Ensure ID matches
        transaction.setDate(request.getDate());
        transaction.setAmount(request.getAmount());
        transaction.setDescription(request.getDescription());
        transaction.setCategory(request.getCategory());
        transaction.setMerchant(request.getMerchant());
        transaction.setBalanceAtTransaction(request.getBalanceAtTransaction());

        return transactionRepo.save(transaction);
    }

    @Transactional
    public void deleteTransaction(Long id) {
        transactionRepo.deleteById(id);
    }

    @Transactional
    public void deleteTransactionsById(List<Long> ids){
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("ids must not be null or empty");
        }
        transactionRepo.deleteAllByIdIn(ids);
    }

    @Transactional
    public void importMultipleCSVs(long userId, MultipartFile file){
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Files must not be empty");
        }

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            List<Transaction> transactions = new ArrayList<>();
            //Skip header
            reader.readLine();

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                // Safely parse accountNo
                int accountNo;
                try {
                    accountNo = Integer.parseInt(parts[2].trim());
                } catch (NumberFormatException e) {
                    continue;
                }
                Account account = handleAccountNoAssign(accountNo, user);

                Transaction txn = new Transaction();
                txn.setDate(LocalDate.parse(parts[0]));
                txn.setAmount(new BigDecimal(parts[1]));
                txn.setAccount(account);
                txn.setDescription(parts[3].trim());
                txn.setBalanceAtTransaction(new BigDecimal (parts[4]));
                txn.setCategory(parts[5].trim());
                txn.setMerchant(parts[6].trim());
                txn.setUser(user);

                transactions.add(txn);
            }
            transactionRepo.saveAll(transactions);

        } catch (IOException e) {
            throw new RuntimeException("Failed to read CSV file", e);
        }
    }

    public Account handleAccountNoAssign(int accountNo, User user) {
        // Handle AccountNo assignment & account creation if not already exists
        return accountRepo.findByAccountNo(accountNo)
                .orElseGet(() -> {
                    Account newAccount = new Account();
                    newAccount.setAccountNo(accountNo);
                    newAccount.setType(AccountType.SPENDING);
                    newAccount.setName("Spending Account");
                    newAccount.setUser(user);
                    return accountRepo.save(newAccount);
                });
    }
}
