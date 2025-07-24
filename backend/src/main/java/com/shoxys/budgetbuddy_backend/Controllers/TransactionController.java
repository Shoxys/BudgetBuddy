package com.shoxys.budgetbuddy_backend.Controllers;

import com.shoxys.budgetbuddy_backend.Config.Constants;
import com.shoxys.budgetbuddy_backend.Assembler.TransactionModelAssembler;
import com.shoxys.budgetbuddy_backend.DTOs.Transaction.TransactionRequest;
import com.shoxys.budgetbuddy_backend.DTOs.Transaction.TransactionSummaryResponse;
import com.shoxys.budgetbuddy_backend.Entities.Transaction;
import com.shoxys.budgetbuddy_backend.Entities.User;
import com.shoxys.budgetbuddy_backend.Exceptions.TransactionNotFoundException;
import com.shoxys.budgetbuddy_backend.Security.AppUserDetails;
import com.shoxys.budgetbuddy_backend.Services.TransactionService;
import com.shoxys.budgetbuddy_backend.Services.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Handles HTTP requests for managing transactions.
 */
@RestController
@RequestMapping(Constants.TRANSACTION_ENDPOINT)
public class TransactionController {
  private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);
  private final TransactionService transactionService;
  private final UserService userService;
  private final TransactionModelAssembler transactionModelAssembler;

  public TransactionController(TransactionService transactionService,
                               UserService userService,
                               TransactionModelAssembler transactionModelAssembler) {
    this.transactionService = transactionService;
    this.userService = userService;
    this.transactionModelAssembler = transactionModelAssembler;
  }

  /**
   * Retrieves all transactions for the authenticated user.
   *
   * @param userDetails the authenticated user's details
   * @return a list of transactions
   */
  @GetMapping("/")
  public ResponseEntity<List<Transaction>> getAllTransactionsForUser(
          @AuthenticationPrincipal AppUserDetails userDetails) {
    String username = validateUserDetails(userDetails);
    logger.info("Fetching all transactions for user: {}", username);
    long userId = userService.getUserIdByEmail(username);
    List<Transaction> transactions = transactionService.getAllTransactionsByUserId(userId);
    logger.info("Retrieved {} transactions for user: {}", transactions.size(), username);
    return ResponseEntity.ok(transactions);
  }

  /**
   * Retrieves transactions for the authenticated user within a time frame.
   *
   * @param userDetails the authenticated user's details
   * @param startDate the start date of the time frame
   * @param endDate the end date of the time frame
   * @return a list of transactions
   */
  @GetMapping("/timeframe")
  public ResponseEntity<List<Transaction>> getTransactionsForUserInTimeFrame(
          @AuthenticationPrincipal AppUserDetails userDetails,
          @RequestParam LocalDate startDate,
          @RequestParam LocalDate endDate) {
    String username = validateUserDetails(userDetails);
    logger.info("Fetching transactions for user: {}, from {} to {}", username, startDate, endDate);
    long userId = userService.getUserIdByEmail(username);
    List<Transaction> transactions = transactionService.getTransactionsByUserIdInTimeFrame(userId, startDate, endDate);
    logger.info("Retrieved {} transactions for user: {}", transactions.size(), username);
    return ResponseEntity.ok(transactions);
  }

  /**
   * Retrieves all transactions for the authenticated user, sorted by oldest first.
   *
   * @param userDetails the authenticated user's details
   * @return a list of transactions
   */
  @GetMapping("/oldest")
  public ResponseEntity<List<Transaction>> getAllOldestTransactionsForUser(
          @AuthenticationPrincipal AppUserDetails userDetails) {
    String username = validateUserDetails(userDetails);
    logger.info("Fetching oldest transactions for user: {}", username);
    long userId = userService.getUserIdByEmail(username);
    List<Transaction> transactions = transactionService.getAllTransactionsByUserIdSortedOldest(userId);
    logger.info("Retrieved {} oldest transactions for user: {}", transactions.size(), username);
    return ResponseEntity.ok(transactions);
  }

  /**
   * Retrieves all transactions for the authenticated user, sorted by newest first.
   *
   * @param userDetails the authenticated user's details
   * @return a list of transactions
   */
  @GetMapping("/newest")
  public ResponseEntity<List<Transaction>> getAllNewestTransactionsForUser(
          @AuthenticationPrincipal AppUserDetails userDetails) {
    String username = validateUserDetails(userDetails);
    logger.info("Fetching newest transactions for user: {}", username);
    long userId = userService.getUserIdByEmail(username);
    List<Transaction> transactions = transactionService.getTransactionsByUserIdSortedNewest(userId);
    logger.info("Retrieved {} newest transactions for user: {}", transactions.size(), username);
    return ResponseEntity.ok(transactions);
  }

  /**
   * Retrieves paginated transactions for the authenticated user.
   *
   * @param userDetails the authenticated user's details
   * @param page the page number
   * @param size the page size
   * @param sort the sort order (e.g., "date,desc")
   * @param pagedResourcesAssembler the assembler for paged resources
   * @return a paged model of transactions
   */
  @GetMapping("/paginated")
  public ResponseEntity<PagedModel<EntityModel<Transaction>>> getTransactionsPaginated(
          @AuthenticationPrincipal AppUserDetails userDetails,
          @RequestParam(defaultValue = "0") int page,
          @RequestParam(defaultValue = "20") int size,
          @RequestParam(defaultValue = "date,desc") String sort,
          PagedResourcesAssembler<Transaction> pagedResourcesAssembler) {
    String username = validateUserDetails(userDetails);
    logger.info("Fetching paginated transactions for user: {}, page: {}, size: {}, sort: {}", username, page, size, sort);
    try {
      User user = userService.getUserByEmail(username);
      Page<Transaction> transactions = transactionService.getTransactionsByUserIdPaginated(user.getId(), page, size, sort);
      PagedModel<EntityModel<Transaction>> pagedModel = pagedResourcesAssembler.toModel(transactions, transactionModelAssembler);
      logger.info("Retrieved {} transactions for user: {}", transactions.getContent().size(), username);
      return ResponseEntity.ok(pagedModel);
    } catch (IllegalArgumentException e) {
      logger.error("Invalid pagination parameters for user: {}: {}", username, e.getMessage());
      return ResponseEntity.badRequest().build();
    } catch (Exception e) {
      logger.error("Failed to fetch paginated transactions for user: {}: {}", username, e.getMessage(), e);
      return ResponseEntity.status(500).build();
    }
  }

  /**
   * Retrieves a transaction summary for the authenticated user by time frame.
   *
   * @param userDetails the authenticated user's details
   * @param timeFrame the time frame for the summary
   * @return the transaction summary
   */
  @GetMapping("/summary")
  public ResponseEntity<TransactionSummaryResponse> getUserTransactionSummary(
          @AuthenticationPrincipal AppUserDetails userDetails, @RequestParam String timeFrame) {
    String username = validateUserDetails(userDetails);
    logger.info("Fetching transaction summary for user: {}, timeFrame: {}", username, timeFrame);
    long userId = userService.getUserIdByEmail(username);
    TransactionSummaryResponse summary = transactionService.getTransactionSummaryByTimeFrame(userId, timeFrame);
    logger.info("Retrieved transaction summary for user: {}", username);
    return ResponseEntity.ok(summary);
  }

  /**
   * Retrieves the current balance for the authenticated user.
   *
   * @param userDetails the authenticated user's details
   * @return the current balance
   */
  @GetMapping("/current-balance")
  public ResponseEntity<BigDecimal> getCurrentBalance(@AuthenticationPrincipal AppUserDetails userDetails) {
    String username = validateUserDetails(userDetails);
    logger.info("Fetching current balance for user: {}", username);
    BigDecimal balance = transactionService.getCurrentBalanceByUser(username);
    logger.info("Retrieved current balance for user: {}", username);
    return ResponseEntity.ok(balance);
  }

  /**
   * Adds a new transaction for the authenticated user.
   *
   * @param userDetails the authenticated user's details
   * @param request the transaction request
   * @return the created transaction
   */
  @PostMapping
  public ResponseEntity<Transaction> addTransaction(
          @AuthenticationPrincipal AppUserDetails userDetails,
          @Valid @RequestBody TransactionRequest request) {
    String username = validateUserDetails(userDetails);
    logger.info("Adding transaction for user: {}", username);
    Transaction transaction = transactionService.addTransaction(username, request);
    logger.info("Added transaction for user: {}, ID: {}", username, transaction.getId());
    return ResponseEntity.ok(transaction);
  }

  /**
   * Updates an existing transaction for the authenticated user.
   *
   * @param userDetails the authenticated user's details
   * @param id the transaction ID
   * @param request the transaction request
   * @return the updated transaction
   */
  @PutMapping("/{id}")
  public ResponseEntity<Transaction> updateTransaction(
          @AuthenticationPrincipal AppUserDetails userDetails,
          @PathVariable long id,
          @Valid @RequestBody TransactionRequest request) {
    String username = validateUserDetails(userDetails);
    logger.info("Updating transaction for user: {}, ID: {}", username, id);
    Transaction transaction = transactionService.updateTransaction(username, id, request);
    logger.info("Updated transaction for user: {}, ID: {}", username, id);
    return ResponseEntity.ok(transaction);
  }

  /**
   * Deletes a transaction for the authenticated user.
   *
   * @param userDetails the authenticated user's details
   * @param id the transaction ID
   * @return a response indicating success
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<String> deleteTransaction(
          @AuthenticationPrincipal AppUserDetails userDetails, @PathVariable long id) {
    String username = validateUserDetails(userDetails);
    logger.info("Deleting transaction for user: {}, ID: {}", username, id);
    transactionService.deleteTransaction(username, id);
    logger.info("Deleted transaction for user: {}, ID: {}", username, id);
    return ResponseEntity.ok("Successfully deleted transaction");
  }

  /**
   * Deletes multiple transactions for the authenticated user.
   *
   * @param userDetails the authenticated user's details
   * @param ids the list of transaction IDs
   * @return a response indicating success
   */
  @DeleteMapping("/delete-selected")
  public ResponseEntity<String> deleteMultipleTransactions(
          @AuthenticationPrincipal AppUserDetails userDetails, @RequestBody List<Long> ids) {
    String username = validateUserDetails(userDetails);
    logger.info("Deleting {} transactions for user: {}", ids.size(), username);
    transactionService.deleteTransactionsById(username, ids);
    logger.info("Deleted {} transactions for user: {}", ids.size(), username);
    return ResponseEntity.ok("Deleted " + ids.size() + " transactions");
  }

  /**
   * Imports transactions from CSV files for the authenticated user.
   *
   * @param userDetails the authenticated user's details
   * @param files the CSV files to import
   * @return a response indicating success or failure
   */
  @PostMapping("/upload")
  public ResponseEntity<String> importTransactions(
          @RequestParam("files") MultipartFile[] files,
          @AuthenticationPrincipal AppUserDetails userDetails) {
    String username = validateUserDetails(userDetails);
    logger.info("Importing {} CSV files for user: {}", files.length, username);
    try {
      User user = userService.getUserByEmail(username);
      for (MultipartFile file : files) {
        if (file.isEmpty()) {
          logger.warn("Skipping empty file: {}", file.getOriginalFilename());
          continue;
        }
        logger.info("Importing file: {}, size: {} bytes", file.getOriginalFilename(), file.getSize());
        transactionService.importMultipleCSVs(user.getId(), file);
        logger.info("Imported file: {}", file.getOriginalFilename());
      }
      logger.info("All CSVs processed for user: {}", username);
      return ResponseEntity.ok("All CSVs imported successfully");
    } catch (IllegalArgumentException e) {
      logger.error("Validation error during CSV import for user: {}: {}", username, e.getMessage());
      return ResponseEntity.badRequest().body("Error importing CSV: " + e.getMessage());
    } catch (Exception e) {
      logger.error("Unexpected error during CSV import for user: {}: {}", username, e.getMessage(), e);
      return ResponseEntity.status(500).body("Unexpected error during CSV import");
    }
  }

  /**
   * Retrieves a transaction by ID for the authenticated user.
   *
   * @param userDetails the authenticated user's details
   * @param id the transaction ID
   * @return the transaction as an entity model
   */
  @GetMapping("/{id}")
  public ResponseEntity<EntityModel<Transaction>> getTransactionById(
          @AuthenticationPrincipal AppUserDetails userDetails, @PathVariable Long id) {
    String username = validateUserDetails(userDetails);
    logger.info("Fetching transaction for user: {}, ID: {}", username, id);
    try {
      Transaction transaction = transactionService.getTransactionById(username, id);
      logger.info("Retrieved transaction for user: {}, ID: {}", username, id);
      return ResponseEntity.ok(transactionModelAssembler.toModel(transaction));
    } catch (TransactionNotFoundException e) {
      logger.error("Transaction not found for user: {}, ID: {}", username, id);
      throw e;
    }
  }

  private String validateUserDetails(AppUserDetails userDetails) {
    if (userDetails == null) {
      logger.warn("Unauthorized transaction request");
      throw new IllegalStateException("User is not authenticated");
    }
    return userDetails.getUsername();
  }
}
