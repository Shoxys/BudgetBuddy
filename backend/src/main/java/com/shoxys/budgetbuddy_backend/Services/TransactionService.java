package com.shoxys.budgetbuddy_backend.Services;

import com.shoxys.budgetbuddy_backend.Config.Constants;
import com.shoxys.budgetbuddy_backend.DTOs.Transaction.TransactionRequest;
import com.shoxys.budgetbuddy_backend.DTOs.Transaction.TransactionSummaryResponse;
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
import com.shoxys.budgetbuddy_backend.Utils.Utils;
import jakarta.transaction.Transactional;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Service for managing transactions, including CRUD operations and CSV imports.
 */
@Service
public class TransactionService {
  private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);
  private final TransactionRepo transactionRepo;
  private final AccountRepo accountRepo;
  private final UserRepo userRepo;
  private final AccountService accountService;

  /**
   * Constructs a TransactionService with required dependencies.
   *
   * @param transactionRepo Repository for transaction-related data access
   * @param accountRepo     Repository for account-related data access
   * @param userRepo        Repository for user-related data access
   * @param accountService  Service for account-related operations
   */
  public TransactionService(TransactionRepo transactionRepo, AccountRepo accountRepo,
                            UserRepo userRepo, AccountService accountService) {
    logger.debug("Initializing TransactionService");
    if (transactionRepo == null || accountRepo == null || userRepo == null || accountService == null) {
      logger.error("One or more dependencies are null");
      throw new IllegalArgumentException("All dependencies must be non-null");
    }
    this.transactionRepo = transactionRepo;
    this.accountRepo = accountRepo;
    this.userRepo = userRepo;
    this.accountService = accountService;
    logger.info("TransactionService initialized successfully");
  }

  /**
   * Retrieves a transaction by ID for the specified user.
   *
   * @param email the user's email
   * @param id    the transaction ID
   * @return the transaction
   * @throws TransactionNotFoundException if the transaction is not found
   * @throws UserNotFoundException if the user is not found
   */
  public Transaction getTransactionById(String email, Long id) {
    logger.info("Fetching transaction for user: {}, ID: {}", email, id);
    User user = validateUser(email);
    return transactionRepo.findTransactionByUserAndId(user, id)
            .orElseThrow(() -> {
              logger.error("Transaction not found for user: {}, ID: {}", email, id);
              return new TransactionNotFoundException(id);
            });
  }

  /**
   * Retrieves all transactions for a user.
   *
   * @param userId the user's ID
   * @return a list of transactions
   */
  public List<Transaction> getAllTransactionsByUserId(long userId) {
    logger.info("Fetching all transactions for user ID: {}", userId);
    if (!userRepo.existsById(userId)) {
      logger.error("User not found: {}", userId);
      throw new UserNotFoundException(userId);
    }
    List<Transaction> transactions = transactionRepo.findByUser_Id(userId);
    logger.info("Retrieved {} transactions for user ID: {}", transactions.size(), userId);
    return transactions;
  }

  /**
   * Retrieves transactions for a user within a date range.
   *
   * @param userId    the user's ID
   * @param startDate the start date
   * @param endDate   the end date
   * @return a list of transactions
   */
  public List<Transaction> getTransactionsByUserIdInTimeFrame(Long userId, LocalDate startDate, LocalDate endDate) {
    logger.info("Fetching transactions for user ID: {}, from {} to {}", userId, startDate, endDate);
    if (!userRepo.existsById(userId)) {
      logger.error("User not found: {}", userId);
      throw new UserNotFoundException(userId);
    }
    validateDateRange(startDate, endDate);
    List<Transaction> transactions = transactionRepo.findByUserIdAndDateBetween(userId, startDate, endDate);
    logger.info("Retrieved {} transactions for user ID: {}", transactions.size(), userId);
    return transactions;
  }

  /**
   * Retrieves paginated transactions for a user.
   *
   * @param userId the user's ID
   * @param page   the page number
   * @param size   the page size
   * @param sort   the sort order (e.g., "date,asc")
   * @return a page of transactions
   */
  public Page<Transaction> getTransactionsByUserIdPaginated(Long userId, int page, int size, String sort) {
    logger.info("Fetching paginated transactions for user ID: {}, page: {}, size: {}, sort: {}", userId, page, size, sort);
    if (!userRepo.existsById(userId)) {
      logger.error("User not found: {}", userId);
      throw new UserNotFoundException(userId);
    }
    if (page < 0) {
      logger.warn("Invalid page number: {}", page);
      throw new IllegalArgumentException("Page must be greater than or equal to 0");
    }
    if (size <= 0) {
      logger.warn("Invalid page size: {}", size);
      throw new IllegalArgumentException("Size must be greater than 0");
    }
    PageRequest pageRequest = PageRequest.of(page, size);
    Page<Transaction> transactions = "date,asc".equalsIgnoreCase(sort) ?
            transactionRepo.findByUser_IdOrderByDateAsc(userId, pageRequest) :
            transactionRepo.findByUser_IdOrderByDateDesc(userId, pageRequest);
    logger.info("Retrieved {} transactions for user ID: {}", transactions.getContent().size(), userId);
    return transactions;
  }

  /**
   * Retrieves all transactions for a user, sorted by oldest first.
   *
   * @param userId the user's ID
   * @return a list of transactions
   */
  public List<Transaction> getAllTransactionsByUserIdSortedOldest(Long userId) {
    logger.info("Fetching oldest transactions for user ID: {}", userId);
    if (!userRepo.existsById(userId)) {
      logger.error("User not found: {}", userId);
      throw new UserNotFoundException(userId);
    }
    List<Transaction> transactions = transactionRepo.findByUser_IdOrderByDateAsc(userId);
    logger.info("Retrieved {} oldest transactions for user ID: {}", transactions.size(), userId);
    return transactions;
  }

  /**
   * Retrieves all transactions for a user, sorted by newest first.
   *
   * @param userId the user's ID
   * @return a list of transactions
   */
  public List<Transaction> getTransactionsByUserIdSortedNewest(Long userId) {
    logger.info("Fetching newest transactions for user ID: {}", userId);
    if (!userRepo.existsById(userId)) {
      logger.error("User not found: {}", userId);
      throw new UserNotFoundException(userId);
    }
    List<Transaction> transactions = transactionRepo.findByUser_IdOrderByDateDesc(userId);
    logger.info("Retrieved {} newest transactions for user ID: {}", transactions.size(), userId);
    return transactions;
  }

  /**
   * Retrieves transactions for a user within a time frame (e.g., "7days").
   *
   * @param userId    the user's ID
   * @param timeFrame the time frame (e.g., "7days", "30days")
   * @return a list of transactions
   */
  public List<Transaction> getAllTransactionsByUserIdTimeFrame(Long userId, String timeFrame) {
    logger.info("Fetching transactions for user ID: {}, timeFrame: {}", userId, timeFrame);
    if (!userRepo.existsById(userId)) {
      logger.error("User not found: {}", userId);
      throw new UserNotFoundException(userId);
    }
    if (timeFrame == null || timeFrame.isEmpty()) {
      logger.warn("Invalid timeFrame: {}", timeFrame);
      throw new IllegalArgumentException("TimeFrame must be valid");
    }
    LocalDate startDate;
    LocalDate endDate = LocalDate.now();
    switch (timeFrame.toLowerCase()) {
      case "weekly":
        startDate = Utils.getStartOfWeek();
        endDate = Utils.getEndOfWeek();
        break;
      case "7days":
        startDate = LocalDate.now().minusDays(7);
        break;
      case "30days":
        startDate = LocalDate.now().minusDays(30);
        break;
      case "60days":
        startDate = LocalDate.now().minusDays(60);
        break;
      case "90days":
        startDate = LocalDate.now().minusDays(90);
        break;
      default:
        startDate = LocalDate.MIN; // "all time"
    }
    List<Transaction> transactions = transactionRepo.findByUserIdAndDateBetween(userId, startDate, endDate);
    logger.info("Retrieved {} transactions for user ID: {}", transactions.size(), userId);
    return transactions;
  }

  /**
   * Retrieves a transaction summary for a user by time frame.
   *
   * @param userId    the user's ID
   * @param timeFrame the time frame (e.g., "7days")
   * @return the transaction summary
   */
  public TransactionSummaryResponse getTransactionSummaryByTimeFrame(Long userId, String timeFrame) {
    logger.info("Fetching transaction summary for user ID: {}, timeFrame: {}", userId, timeFrame);
    Utils.validatePositiveId(userId, "User ID must be positive");
    if (Utils.nullOrEmpty(timeFrame)) {
      logger.error("Invalid timeFrame: null or empty");
      throw new IllegalArgumentException("TimeFrame must not be null or empty");
    }
    if (!userRepo.existsById(userId)) {
      logger.error("User not found: {}", userId);
      throw new UserNotFoundException("User not found: " + userId);
    }
    List<Transaction> transactions = getAllTransactionsByUserIdTimeFrame(userId, timeFrame);
    if (Utils.nullOrEmpty(transactions)) {
      logger.info("No transactions found for user ID: {}, timeFrame: {}", userId, timeFrame);
      return new TransactionSummaryResponse(null, null, null);
    }
    LocalDate earliest = transactions.stream()
            .map(Transaction::getDate)
            .filter(Objects::nonNull)
            .min(LocalDate::compareTo)
            .orElse(null);
    LocalDate latest = transactions.stream()
            .map(Transaction::getDate)
            .filter(Objects::nonNull)
            .max(LocalDate::compareTo)
            .orElse(null);
    logger.info("Retrieved transaction summary for user ID: {}, count: {}", userId, transactions.size());
    return new TransactionSummaryResponse(transactions.size(), earliest, latest);
  }

  /**
   * Retrieves the current balance for a user's spending account.
   *
   * @param email the user's email
   * @return the current balance
   */
  public BigDecimal getCurrentBalanceByUser(String email) {
    logger.info("Fetching current balance for user: {}", email);
    User user = validateUser(email);
    Account spendingAccount = accountRepo.findAccountByUserAndType(user, AccountType.SPENDING)
            .orElseGet(() -> {
              logger.info("No spending account found for user: {}, creating new", email);
              return accountService.createSpendingAccount(user, BigDecimal.ZERO);
            });
    logger.info("Retrieved balance for user: {}", email);
    return spendingAccount.getBalance();
  }

  /**
   * Adds a new transaction for a user.
   *
   * @param email   the user's email
   * @param request the transaction request
   * @return the created transaction
   */
  @Transactional
  public Transaction addTransaction(String email, TransactionRequest request) {
    logger.info("Adding transaction for user: {}", email);
    User user = validateUser(email);
    Account spendingAccount = accountRepo.findAccountByUserAndType(user, AccountType.SPENDING)
            .orElseGet(() -> {
              logger.info("No spending account found for user: {}, creating new", email);
              return accountService.createSpendingAccount(user, request.getBalanceAtTransaction());
            });
    TransactionType type = request.getType();
    BigDecimal amount = type == TransactionType.DEBIT ? request.getAmount().negate() : request.getAmount();
    BigDecimal newBalance = spendingAccount.getBalance().add(amount);
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
            user);
    Transaction savedTransaction = transactionRepo.save(newTransaction);
    logger.info("Added transaction for user: {}, ID: {}", email, savedTransaction.getId());
    return savedTransaction;
  }

  /**
   * Updates an existing transaction for a user.
   *
   * @param email   the user's email
   * @param id      the transaction ID
   * @param request the transaction request
   * @return the updated transaction
   */
  @Transactional
  public Transaction updateTransaction(String email, long id, TransactionRequest request) {
    logger.info("Updating transaction for user: {}, ID: {}", email, id);
    User user = validateUser(email);
    Transaction transaction = transactionRepo.findTransactionByUserAndId(user, id)
            .orElseThrow(() -> {
              logger.error("Transaction not found for user: {}, ID: {}", email, id);
              return new TransactionNotFoundException(id);
            });
    TransactionType type = request.getType();
    BigDecimal newAmount = type == TransactionType.DEBIT ? request.getAmount().negate() : request.getAmount();
    Account account = accountService.syncSpendingAccountBalance(transaction, newAmount);
    transaction.setDate(request.getDate());
    transaction.setAmount(newAmount);
    transaction.setDescription(request.getDescription());
    transaction.setCategory(request.getCategory());
    transaction.setMerchant(request.getMerchant());
    transaction.setBalanceAtTransaction(account.getBalance());
    Transaction updatedTransaction = transactionRepo.save(transaction);
    logger.info("Updated transaction for user: {}, ID: {}", email, id);
    return updatedTransaction;
  }

  /**
   * Deletes a transaction for a user.
   *
   * @param email the user's email
   * @param id    the transaction ID
   */
  @Transactional
  public void deleteTransaction(String email, Long id) {
    logger.info("Deleting transaction for user: {}, ID: {}", email, id);
    User user = validateUser(email);
    Transaction transaction = transactionRepo.findTransactionByUserAndId(user, id)
            .orElseThrow(() -> {
              logger.error("Transaction not found for user: {}, ID: {}", email, id);
              return new TransactionNotFoundException(id);
            });
    Account account = transaction.getAccount();
    account.setBalance(account.getBalance().subtract(transaction.getAmount()));
    accountRepo.save(account);
    transactionRepo.delete(transaction);
    logger.info("Deleted transaction for user: {}, ID: {}", email, id);
  }

  /**
   * Deletes multiple transactions for a user.
   *
   * @param email the user's email
   * @param ids   the list of transaction IDs
   */
  @Transactional
  public void deleteTransactionsById(String email, List<Long> ids) {
    logger.info("Deleting {} transactions for user: {}", ids.size(), email);
    if (Utils.nullOrEmpty(ids)) {
      logger.warn("Invalid transaction IDs: null or empty");
      throw new IllegalArgumentException("IDs must not be null or empty");
    }
    User user = validateUser(email);
    Account account = accountRepo.findAccountByUserAndType(user, AccountType.SPENDING)
            .orElseThrow(() -> {
              logger.error("No spending account found for user: {}", email);
              return new AccountNotFoundException();
            });
    transactionRepo.deleteAllByIdInAndUser(ids, user);
    accountService.recalculateBalanceForSpendingAccount(account);
    logger.info("Deleted {} transactions for user: {}", ids.size(), email);
  }

  /**
   * Imports transactions from a CSV file for a user.
   *
   * @param userId the user's ID
   * @param file   the CSV file
   * @return the list of imported transactions
   * @throws IllegalArgumentException if the file is invalid
   * @throws RuntimeException if CSV parsing fails
   */
  @Transactional
  public List<Transaction> importMultipleCSVs(long userId, MultipartFile file) {
    validateFile(file);
    logger.info("Importing CSV file for user ID: {}, file: {}", userId, file.getOriginalFilename());
    User user = validateUserById(userId);
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
      String[] headers = parseHeaders(reader);
      CSVFormat format = CSVFormat.DEFAULT.withHeader(headers);
      CSVParser csvParser = new CSVParser(reader, format);
      List<Transaction> transactions = parseTransactions(csvParser, user);
      transactionRepo.saveAll(transactions);
      if (!transactions.isEmpty()) {
        Account account = accountRepo.findAccountByUserAndType(user, AccountType.SPENDING)
                .orElseThrow(() -> {
                  logger.error("No spending account found for user ID: {}", userId);
                  return new AccountNotFoundException();
                });
        accountService.recalculateBalanceForSpendingAccount(account);
      }
      logger.info("Imported {} transactions for user ID: {}", transactions.size(), userId);
      return transactions;
    } catch (IOException e) {
      logger.error("Failed to read CSV file for user ID: {}: {}", userId, e.getMessage(), e);
      throw new RuntimeException("Failed to read CSV file: " + file.getOriginalFilename(), e);
    }
  }

  private void validateFile(MultipartFile file) {
    if (file == null || file.isEmpty()) {
      logger.warn("Invalid file: null or empty");
      throw new IllegalArgumentException("File must not be empty");
    }
    if (!Constants.CSV_CONTENT_TYPE.equalsIgnoreCase(file.getContentType())) {
      logger.warn("Invalid file type: {}", file.getContentType());
      throw new IllegalArgumentException("File must be a CSV");
    }
    if (!file.getOriginalFilename().toLowerCase().endsWith(Constants.CSV_FILE_EXTENSION)) {
      logger.warn("Invalid file extension: {}", file.getOriginalFilename());
      throw new IllegalArgumentException("File must have a .csv extension");
    }
  }

  private String[] parseHeaders(BufferedReader reader) throws IOException {
    String headerLine = reader.readLine();
    if (headerLine == null) {
      logger.warn("CSV file is empty or missing header");
      throw new IllegalArgumentException("CSV file is empty or missing header");
    }
    String[] headers = headerLine.split(Constants.CSV_DELIMITER);
    for (int i = 0; i < headers.length; i++) {
      if (headers[i].trim().isEmpty()) {
        headers[i] = BankCsvColumn.UNUSED.getStr() + i;
      }
    }
    logger.debug("Parsed CSV headers: {}", String.join(", ", headers));
    return headers;
  }

  private LocalDate parseDate(String rawDate) {
    logger.debug("Parsing date: {}", rawDate);
    for (DateTimeFormatter formatter : Constants.CSV_DATE_FORMATTERS) {
      try {
        LocalDate date = LocalDate.parse(rawDate, formatter);
        logger.debug("Parsed date: {} as {}", rawDate, date);
        return date;
      } catch (DateTimeParseException e) {
        // Continue to try next formatter
      }
    }
    logger.warn("Invalid date format in CSV: {}", rawDate);
    throw new IllegalArgumentException("Invalid date format in CSV: " + rawDate + ". Supported formats: " +
            Constants.CSV_DATE_FORMATTERS.stream()
                    .map(DateTimeFormatter::toString)
                    .collect(Collectors.joining(", ")));
  }

  private Transaction createTransactionForCsv(CSVRecord record, User user) {
    logger.debug("Creating transaction from CSV record: {}", record);
    Account account = accountService.handleFetchAccount(user);
    LocalDate date = parseDate(record.get(BankCsvColumn.DATE.getStr()));
    String merchant = record.isSet(BankCsvColumn.MERCHANT.getStr()) && !record.get(BankCsvColumn.MERCHANT.getStr()).isEmpty()
            ? record.get(BankCsvColumn.MERCHANT.getStr()).trim()
            : null;
    BigDecimal amount;
    try {
      amount = new BigDecimal(record.get(BankCsvColumn.AMOUNT.getStr()).trim());
      logger.debug("Parsed amount: {}", amount);
    } catch (NumberFormatException e) {
      logger.warn("Invalid amount format in CSV: {}", record.get(BankCsvColumn.AMOUNT.getStr()));
      throw new IllegalArgumentException("Invalid amount format in CSV: " + record.get(BankCsvColumn.AMOUNT.getStr()));
    }
    BigDecimal balanceAtTransaction;
    try {
      balanceAtTransaction = new BigDecimal(record.get(BankCsvColumn.BALANCE.getStr()).trim());
      logger.debug("Parsed balance: {}", balanceAtTransaction);
    } catch (NumberFormatException e) {
      logger.warn("Invalid balance format in CSV: {}", record.get(BankCsvColumn.BALANCE.getStr()));
      throw new IllegalArgumentException("Invalid balance format in CSV: " + record.get(BankCsvColumn.BALANCE.getStr()));
    }
    Transaction transaction = new Transaction();
    transaction.setDate(date);
    transaction.setAmount(amount);
    transaction.setAccount(account);
    transaction.setDescription(record.get(BankCsvColumn.DETAILS.getStr()).trim());
    transaction.setBalanceAtTransaction(balanceAtTransaction);
    transaction.setCategory(record.get(BankCsvColumn.CATEGORY.getStr()).trim());
    transaction.setMerchant(merchant);
    transaction.setUser(user);
    transaction.setSource(SourceType.CSV);
    logger.debug("Created transaction: date={}, amount={}, merchant={}", date, amount, merchant);
    return transaction;
  }

  private List<Transaction> parseTransactions(CSVParser csvParser, User user) {
    logger.debug("Parsing CSV transactions for user ID: {}", user.getId());
    List<Transaction> transactions = new ArrayList<>();
    for (CSVRecord record : csvParser) {
      transactions.add(createTransactionForCsv(record, user));
    }
    logger.debug("Parsed {} transactions from CSV", transactions.size());
    return transactions;
  }

  private User validateUser(String email) {
    if (Utils.nullOrEmpty(email)) {
      logger.warn("Invalid email: null or empty");
      throw new IllegalArgumentException("Email must not be null or empty");
    }
    return userRepo.getUserByEmail(email).orElseThrow(() -> {
      logger.error("User not found: {}", email);
      return new UserNotFoundException(email);
    });
  }

  private User validateUserById(Long userId) {
    if (userId == null) {
      logger.warn("Invalid user ID: null");
      throw new IllegalArgumentException("User ID must not be null");
    }
    return userRepo.findById(userId).orElseThrow(() -> {
      logger.error("User not found: {}", userId);
      return new UserNotFoundException(userId);
    });
  }

  private void validateDateRange(LocalDate startDate, LocalDate endDate) {
    if (startDate == null) {
      logger.warn("Invalid startDate: null");
      throw new IllegalArgumentException("Start date must be valid");
    }
    if (endDate == null) {
      logger.warn("Invalid endDate: null");
      throw new IllegalArgumentException("End date must be valid");
    }
    if (startDate.isAfter(endDate)) {
      logger.warn("Invalid date range: startDate {} is after endDate {}", startDate, endDate);
      throw new InvalidDateRangeException(startDate, endDate);
    }
  }
}