package com.shoxys.budgetbuddy_backend.Controllers;

import com.shoxys.budgetbuddy_backend.DTOs.TransactionRequest;
import com.shoxys.budgetbuddy_backend.DTOs.TransactionSummaryResponse;
import com.shoxys.budgetbuddy_backend.Entities.Transaction;
import com.shoxys.budgetbuddy_backend.Security.AppUserDetails;
import com.shoxys.budgetbuddy_backend.Services.TransactionService;
import com.shoxys.budgetbuddy_backend.Services.UserService;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
  private final TransactionService transactionService;
  private final UserService userService;

  public TransactionController(TransactionService transactionService, UserService userService) {
    this.transactionService = transactionService;
    this.userService = userService;
  }

  @GetMapping("/")
  public List<Transaction> getAllTransactionsForUser(
      @AuthenticationPrincipal UserDetails userDetails) {
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
  public List<Transaction> getAllOldestTransactionsForUser(
      @AuthenticationPrincipal UserDetails userDetails) {
    long userId = userService.getUserIdByEmail(userDetails.getUsername());
    return transactionService.getAllTransactionsByUserIdSortedOldest(userId);
  }

  @GetMapping("/newest")
  public List<Transaction> getAllNewestTransactionsForUser(
      @AuthenticationPrincipal UserDetails userDetails) {
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
      @AuthenticationPrincipal UserDetails userDetails, @RequestParam String timeFrame) {
    long userId = userService.getUserIdByEmail(userDetails.getUsername());
    return transactionService.getTransactionSummaryByTimeFrame(userId, timeFrame);
  }

  @GetMapping("/current-balance")
  public BigDecimal getCurrentBalance(@AuthenticationPrincipal UserDetails userDetails) {
    return transactionService.getCurrentBalanceByUser(userDetails.getUsername());
  }

  @PostMapping
  public Transaction addTransaction(
      @AuthenticationPrincipal AppUserDetails userDetails,
      @Valid @RequestBody TransactionRequest request) {
    return transactionService.addTransaction(userDetails.getUsername(), request);
  }

  @PutMapping("/{id}")
  public Transaction updateTransaction(
      @AuthenticationPrincipal AppUserDetails userDetails,
      @PathVariable long id,
      @Valid @RequestBody TransactionRequest request) {
    return transactionService.updateTransaction(userDetails.getUsername(), id, request);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<String> deleteTransaction(
      @AuthenticationPrincipal AppUserDetails userDetails, @PathVariable long id) {
    transactionService.deleteTransaction(userDetails.getUsername(), id);
    return ResponseEntity.ok("Successfully deleted transaction");
  }

  @DeleteMapping("/delete-selected")
  public ResponseEntity<String> deleteMultipleTransactions(
      @AuthenticationPrincipal AppUserDetails userDetails, @RequestBody List<Long> ids) {
    transactionService.deleteTransactionsById(userDetails.getUsername(), ids);
    return ResponseEntity.ok("Deleted " + ids.size() + " transactions");
  }

  @PostMapping("/upload")
  public ResponseEntity<String> importTransactions(
      @RequestParam("files") MultipartFile[] files,
      @AuthenticationPrincipal AppUserDetails userDetails) {
    long userId = userService.getUserIdByEmail(userDetails.getUsername());
    for (MultipartFile file : files) {
      transactionService.importMultipleCSVs(userId, file);
    }
    return ResponseEntity.ok("All CSVs imported successfully");
  }
}
