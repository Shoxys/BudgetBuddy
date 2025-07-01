package com.shoxys.budgetbuddy_backend.Controller;

import com.shoxys.budgetbuddy_backend.Entities.Transaction;
import com.shoxys.budgetbuddy_backend.Services.TransactionService;
import com.shoxys.budgetbuddy_backend.Services.UserService;
import com.shoxys.budgetbuddy_backend.dto.TransactionSummaryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    @Autowired
    TransactionService transactionService;
    UserService userService;

    @GetMapping("/user/{userId}")
    public List<Transaction> getAllTransactionsForUser(@PathVariable Long userId) {
        return transactionService.getAllTransactionsByUserId(userId);
    }

    @GetMapping("/user/{userId}/timeframe")
    public List<Transaction> getTransactionsForUserInTimeFrame(
            @PathVariable Long userId,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        return transactionService.getTransactionsByUserIdInTimeFrame(userId, startDate, endDate);
    }

    @GetMapping("/user/{userId}/oldest")
    public List<Transaction> getAllOldestTransactionsForUser(@PathVariable Long userId) {
        return transactionService.getAllTransactionsByUserIdSortedOldest(userId);
    }

    @GetMapping("/user/{userId}/newest")
    public List<Transaction> getAllNewestTransactionsForUser(@PathVariable Long userId) {
        return transactionService.getTransactionsByUserIdSortedNewest(userId);
    }

    @GetMapping("/user/{userId}/paginated")
    public Page<Transaction> getPaginatedTransactionsForUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        return transactionService.getTransactionsByUserIdPaginated(userId, page, size);
    }

    @GetMapping("/user/{userId}/summary")
    public TransactionSummaryResponse getUserTransactionSummary(
            @PathVariable Long userId,
            @RequestParam LocalDate start,
            @RequestParam LocalDate end) {

        return transactionService.getUserTransactionSummary(userId, start, end);
    }

    @PostMapping
    public Transaction addTransaction(@RequestBody Transaction transaction) {
        return transactionService.addTransaction(transaction);
    }

    @PutMapping("/{id}")
    public Transaction updateTransaction(@PathVariable  long id,  @RequestBody Transaction transaction) {
        return transactionService.updateTransaction(id, transaction);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteTransaction(@PathVariable long id) {
        transactionService.deleteTransaction(id);
        return ResponseEntity.ok("Successfully deleted transaction");
    }

    @DeleteMapping("/{delete-selected}")
    public ResponseEntity deleteTransactions(@PathVariable List<Long> ids) {
        transactionService.deleteTransactionsById(ids);
        return ResponseEntity.ok("Deleted " + ids.size() + " transactions");
    }

    @PostMapping("/upload")
    public ResponseEntity<String> importTransactions(@RequestParam("files") MultipartFile[] files,
                                                     @AuthenticationPrincipal UserDetails userDetails) {
        long userId = userService.getUserIdByEmail(userDetails.getUsername());
        for (MultipartFile file : files) {
            transactionService.importMultipleCSVs(userId, file);
        }
        return ResponseEntity.ok("All CSVs imported successfully");
    }
}
