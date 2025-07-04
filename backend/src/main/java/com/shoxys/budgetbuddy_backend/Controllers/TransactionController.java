package com.shoxys.budgetbuddy_backend.Controllers;

import com.shoxys.budgetbuddy_backend.Entities.Transaction;
import com.shoxys.budgetbuddy_backend.Security.AppUserDetails;
import com.shoxys.budgetbuddy_backend.Services.TransactionService;
import com.shoxys.budgetbuddy_backend.Services.UserService;
import com.shoxys.budgetbuddy_backend.DTOs.TransactionSummaryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    @Autowired
    TransactionService transactionService;
    @Autowired
    UserService userService;

    @GetMapping("/")
    public List<Transaction> getAllTransactionsForUser(@AuthenticationPrincipal UserDetails userDetails) {
        long userId = userService.getUserIdByEmail(userDetails.getUsername());
        return transactionService.getAllTransactionsByUserId(userId);
    }

    @GetMapping("/timeframe")
    public List<Transaction> getTransactionsForUserInTimeFrame(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        long userId = userService.getUserIdByEmail(userDetails.getUsername());
        return transactionService.getTransactionsByUserIdInTimeFrame(userId, startDate, endDate);
    }

    @GetMapping("/oldest")
    public List<Transaction> getAllOldestTransactionsForUser(@AuthenticationPrincipal UserDetails userDetails) {
        long userId = userService.getUserIdByEmail(userDetails.getUsername());
        return transactionService.getAllTransactionsByUserIdSortedOldest(userId);
    }

    @GetMapping("/newest")
    public List<Transaction> getAllNewestTransactionsForUser(@AuthenticationPrincipal UserDetails userDetails) {
        long userId = userService.getUserIdByEmail(userDetails.getUsername());
        return transactionService.getTransactionsByUserIdSortedNewest(userId);
    }

    @GetMapping("/paginated")
    public Page<Transaction> getPaginatedTransactionsForUser(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        long userId = userService.getUserIdByEmail(userDetails.getUsername());
        return transactionService.getTransactionsByUserIdPaginated(userId, page, size);
    }

    @GetMapping("/summary")
    public TransactionSummaryResponse getUserTransactionSummary(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam LocalDate start,
            @RequestParam LocalDate end) {
        long userId = userService.getUserIdByEmail(userDetails.getUsername());
        return transactionService.getUserTransactionSummary(userId, start, end);
    }

    @PostMapping
    public Transaction addTransaction(@RequestBody Transaction transaction) {
        return transactionService.addTransaction(transaction);
    }

    @PutMapping("/{id}")
    public Transaction updateTransaction(@PathVariable long id,  @RequestBody Transaction transaction) {
        return transactionService.updateTransaction(id, transaction);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTransaction(@PathVariable long id) {
        transactionService.deleteTransaction(id);
        return ResponseEntity.ok("Successfully deleted transaction");
    }

    @DeleteMapping("/delete-selected")
    public ResponseEntity<String>  deleteMultipleTransactions(@RequestBody  List<Long> ids) {
        transactionService.deleteTransactionsById(ids);
        return ResponseEntity.ok("Deleted " + ids.size() + " transactions");
    }

    @PostMapping("/upload")
    public ResponseEntity<String> importTransactions(@RequestParam("files") MultipartFile[] files,
                                                     @AuthenticationPrincipal AppUserDetails userDetails) {
        long userId = userService.getUserIdByEmail(userDetails.getUsername());
        for (MultipartFile file : files) {
            transactionService.importMultipleCSVs(userId, file);
        }
        return ResponseEntity.ok("All CSVs imported successfully");
    }
}
