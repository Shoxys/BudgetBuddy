package com.shoxys.budgetbuddy_backend.Controllers;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shoxys.budgetbuddy_backend.Assembler.TransactionModelAssembler;
import com.shoxys.budgetbuddy_backend.DTOs.Transaction.TransactionRequest;
import com.shoxys.budgetbuddy_backend.DTOs.Transaction.TransactionSummaryResponse;
import com.shoxys.budgetbuddy_backend.Entities.Transaction;
import com.shoxys.budgetbuddy_backend.Entities.User;
import com.shoxys.budgetbuddy_backend.Enums.SourceType;
import com.shoxys.budgetbuddy_backend.Exceptions.TransactionNotFoundException;
import com.shoxys.budgetbuddy_backend.Repo.TransactionRepo;
import com.shoxys.budgetbuddy_backend.Security.AppUserDetails;
import com.shoxys.budgetbuddy_backend.Services.TransactionService;
import com.shoxys.budgetbuddy_backend.Services.UserService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.multipart.MultipartFile;

@WebMvcTest(
    value = TransactionController.class,
    excludeFilters =
        @ComponentScan.Filter(
            type = FilterType.ASSIGNABLE_TYPE,
            classes = com.shoxys.budgetbuddy_backend.Security.JwtAuthFilter.class))
@AutoConfigureMockMvc(addFilters = false)
public class TransactionControllerTest {

  @Autowired private MockMvc mockMvc;
  @MockitoBean private TransactionService transactionService;
  @MockitoBean private UserService userService;
  @MockitoBean private TransactionRepo transactionRepo;
  @MockitoBean private TransactionModelAssembler transactionModelAssembler;
  @MockitoBean private PagedResourcesAssembler<Transaction> pagedResourcesAssembler;
  @Autowired private ObjectMapper objectMapper;

  private final String testEmail = "test@example.com";
  private AppUserDetails principal;

  @BeforeEach
  void setUp() {
    User mockUser = new User(testEmail, "testPassword");
    principal = new AppUserDetails(mockUser);
    SecurityContext context = SecurityContextHolder.createEmptyContext();
    context.setAuthentication(
        new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));
    SecurityContextHolder.setContext(context);
    when(userService.getUserIdByEmail(testEmail)).thenReturn(1L);
  }

  @Test
  void testGetAllTransactionsForUser_Returns200AndTransactionList() throws Exception {
    Transaction transaction = new Transaction();
    transaction.setId(1L);
    transaction.setDate(LocalDate.now());
    transaction.setAmount(BigDecimal.valueOf(100));
    List<Transaction> transactions = Collections.singletonList(transaction);
    when(transactionService.getAllTransactionsByUserId(eq(1L))).thenReturn(transactions);

    mockMvc
        .perform(get("/api/transactions/").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$[0].id").value(1))
        .andExpect(jsonPath("$[0].amount").value(100.0));

    verify(transactionService, times(1)).getAllTransactionsByUserId(eq(1L));
    verify(userService, times(1)).getUserIdByEmail(eq(testEmail));
  }

  @Test
  void testGetTransactionsForUserInTimeFrame_Returns200AndTransactionList() throws Exception {
    LocalDate startDate = LocalDate.of(2025, 7, 1);
    LocalDate endDate = LocalDate.of(2025, 7, 15);
    Transaction transaction = new Transaction();
    transaction.setId(1L);
    transaction.setDate(LocalDate.now());
    transaction.setAmount(BigDecimal.valueOf(100));
    List<Transaction> transactions = Collections.singletonList(transaction);
    when(transactionService.getTransactionsByUserIdInTimeFrame(eq(1L), eq(startDate), eq(endDate)))
        .thenReturn(transactions);

    mockMvc
        .perform(
            get("/api/transactions/timeframe")
                .param("startDate", startDate.toString())
                .param("endDate", endDate.toString())
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$[0].id").value(1))
        .andExpect(jsonPath("$[0].amount").value(100.0));

    verify(transactionService, times(1))
        .getTransactionsByUserIdInTimeFrame(eq(1L), eq(startDate), eq(endDate));
    verify(userService, times(1)).getUserIdByEmail(eq(testEmail));
  }

  @Test
  void testGetAllOldestTransactionsForUser_Returns200AndTransactionList() throws Exception {
    Transaction transaction = new Transaction();
    transaction.setId(1L);
    transaction.setDate(LocalDate.now());
    transaction.setAmount(BigDecimal.valueOf(100));
    List<Transaction> transactions = Collections.singletonList(transaction);
    when(transactionService.getAllTransactionsByUserIdSortedOldest(eq(1L)))
        .thenReturn(transactions);

    mockMvc
        .perform(get("/api/transactions/oldest").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$[0].id").value(1))
        .andExpect(jsonPath("$[0].amount").value(100.0));

    verify(transactionService, times(1)).getAllTransactionsByUserIdSortedOldest(eq(1L));
    verify(userService, times(1)).getUserIdByEmail(eq(testEmail));
  }

  @Test
  void testGetAllNewestTransactionsForUser_Returns200AndTransactionList() throws Exception {
    Transaction transaction = new Transaction();
    transaction.setId(1L);
    transaction.setDate(LocalDate.now());
    transaction.setAmount(BigDecimal.valueOf(100));
    List<Transaction> transactions = Collections.singletonList(transaction);
    when(transactionService.getTransactionsByUserIdSortedNewest(eq(1L))).thenReturn(transactions);

    mockMvc
        .perform(get("/api/transactions/newest").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$[0].id").value(1))
        .andExpect(jsonPath("$[0].amount").value(100.0));

    verify(transactionService, times(1)).getTransactionsByUserIdSortedNewest(eq(1L));
    verify(userService, times(1)).getUserIdByEmail(eq(testEmail));
  }

  @Test
  void testGetPaginatedTransactionsForUser_Returns200AndPage() throws Exception {
    // Arrange
    Transaction transaction = new Transaction();
    transaction.setId(1L);
    transaction.setDate(LocalDate.now());
    transaction.setAmount(BigDecimal.valueOf(100));
    Page<Transaction> page = new PageImpl<>(Collections.singletonList(transaction));
    User mockUser = new User(testEmail, "testPassword");
    mockUser.setId(1L);
    when(userService.getUserByEmail(testEmail)).thenReturn(mockUser);
    when(transactionService.getTransactionsByUserIdPaginated(
            eq(1L), eq(0), eq(20), eq("date,desc")))
        .thenReturn(page);
    when(transactionModelAssembler.toModel(any(Transaction.class)))
        .thenReturn(EntityModel.of(transaction));
    PagedModel<EntityModel<Transaction>> pagedModel =
        PagedModel.of(
            Collections.singletonList(EntityModel.of(transaction)),
            new PagedModel.PageMetadata(20, 0, 1, 1));
    when(pagedResourcesAssembler.toModel(eq(page), eq(transactionModelAssembler)))
        .thenReturn(pagedModel);

    // Act & Assert
    MvcResult result =
        mockMvc
            .perform(
                get("/api/transactions/paginated")
                    .param("page", "0")
                    .param("size", "20")
                    .param("sort", "date,desc")
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/hal+json"))
            .andExpect(jsonPath("$._embedded.transactionList[0].id").value(1))
            .andExpect(jsonPath("$._embedded.transactionList[0].amount").value(100.0))
            .andExpect(jsonPath("$.page.totalElements").value(1))
            .andExpect(jsonPath("$.page.totalPages").value(1))
            .andReturn();

    verify(transactionService, times(1))
        .getTransactionsByUserIdPaginated(eq(1L), eq(0), eq(20), eq("date,desc"));
    verify(userService, times(1)).getUserByEmail(eq(testEmail));
  }

  @Test
  void testGetTransactionById_Returns200AndTransaction() throws Exception {
    // Arrange
    Long id = 1L;
    Transaction transaction = new Transaction();
    transaction.setId(id);
    transaction.setDate(LocalDate.now());
    transaction.setAmount(BigDecimal.valueOf(100));
    EntityModel<Transaction> transactionModel = EntityModel.of(transaction);
    when(transactionService.getTransactionById(eq(testEmail), eq(id))).thenReturn(transaction);
    when(transactionModelAssembler.toModel(transaction)).thenReturn(transactionModel);

    // Act & Assert
    mockMvc
        .perform(get("/api/transactions/{id}", id).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/hal+json"))
        .andExpect(jsonPath("$.id").value(id))
        .andExpect(jsonPath("$.amount").value(100.0));

    verify(transactionService, times(1)).getTransactionById(eq(testEmail), eq(id));
    verify(transactionModelAssembler, times(1)).toModel(eq(transaction));
  }

  @Test
  void testGetTransactionById_Returns404WhenNotFound() throws Exception {
    // Arrange
    Long id = 1L;
    when(transactionService.getTransactionById(eq(testEmail), eq(id)))
        .thenThrow(new TransactionNotFoundException(id));

    // Act & Assert
    mockMvc
        .perform(get("/api/transactions/{id}", id).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());

    verify(transactionService, times(1)).getTransactionById(eq(testEmail), eq(id));
    verify(transactionModelAssembler, never()).toModel(any());
  }

  @Test
  void testGetUserTransactionSummary_Returns200AndSummary() throws Exception {
    TransactionSummaryResponse summary = new TransactionSummaryResponse();
    summary.setCount(23);
    summary.setEarliest(LocalDate.of(2025, 1, 1));
    summary.setLatest(LocalDate.of(2025, 1, 10));
    when(transactionService.getTransactionSummaryByTimeFrame(eq(1L), eq("30days")))
        .thenReturn(summary);

    mockMvc
        .perform(
            get("/api/transactions/summary")
                .param("timeFrame", "30days")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.count").value(23))
        .andExpect(jsonPath("$.earliest").value("2025-01-01"))
        .andExpect(jsonPath("$.latest").value("2025-01-10"));

    verify(transactionService, times(1)).getTransactionSummaryByTimeFrame(eq(1L), eq("30days"));
    verify(userService, times(1)).getUserIdByEmail(eq(testEmail));
  }

  @Test
  void testGetCurrentBalance_Returns200AndBalance() throws Exception {
    when(transactionService.getCurrentBalanceByUser(testEmail))
        .thenReturn(BigDecimal.valueOf(1000.0));

    mockMvc
        .perform(get("/api/transactions/current-balance").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").value(1000.0));

    verify(transactionService, times(1)).getCurrentBalanceByUser(testEmail);
  }

  @Test
  void testAddTransaction_Returns200AndTransaction() throws Exception {
    TransactionRequest request = new TransactionRequest();
    request.setDate(LocalDate.now());
    request.setAmount(BigDecimal.valueOf(100));
    request.setSource(SourceType.MANUAL);
    Transaction transaction = new Transaction();
    transaction.setId(1L);
    transaction.setDate(LocalDate.now());
    transaction.setAmount(BigDecimal.valueOf(100));
    when(transactionService.addTransaction(eq(testEmail), any(TransactionRequest.class)))
        .thenReturn(transaction);

    mockMvc
        .perform(
            post("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.amount").value(100.0));

    verify(transactionService, times(1))
        .addTransaction(eq(testEmail), any(TransactionRequest.class));
  }

  @Test
  void testUpdateTransaction_Returns200AndTransaction() throws Exception {
    long id = 1L;
    TransactionRequest request = new TransactionRequest();
    request.setDate(LocalDate.now());
    request.setAmount(BigDecimal.valueOf(200));
    request.setSource(SourceType.MANUAL);
    Transaction transaction = new Transaction();
    transaction.setId(id);
    transaction.setDate(LocalDate.now());
    transaction.setAmount(BigDecimal.valueOf(200));
    when(transactionService.updateTransaction(eq(testEmail), eq(id), any(TransactionRequest.class)))
        .thenReturn(transaction);

    mockMvc
        .perform(
            put("/api/transactions/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value(id))
        .andExpect(jsonPath("$.amount").value(200.0));

    verify(transactionService, times(1))
        .updateTransaction(eq(testEmail), eq(id), any(TransactionRequest.class));
  }

  @Test
  void testDeleteTransaction_Returns200AndMessage() throws Exception {
    long id = 1L;
    doNothing().when(transactionService).deleteTransaction(eq(testEmail), eq(id));

    mockMvc
        .perform(delete("/api/transactions/{id}", id))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
        .andExpect(content().string("Successfully deleted transaction"));

    verify(transactionService, times(1)).deleteTransaction(eq(testEmail), eq(id));
  }

  @Test
  void testDeleteMultipleTransactions_Returns200AndMessage() throws Exception {
    List<Long> ids = List.of(1L, 2L);
    doNothing().when(transactionService).deleteTransactionsById(eq(testEmail), eq(ids));

    mockMvc
        .perform(
            delete("/api/transactions/delete-selected")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ids)))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
        .andExpect(content().string("Deleted 2 transactions"));

    verify(transactionService, times(1)).deleteTransactionsById(eq(testEmail), eq(ids));
  }

  @Test
  void testImportTransactions_Returns200AndMessage() throws Exception {
    // Arrange
    Transaction transaction = new Transaction();
    transaction.setId(1L);
    transaction.setDate(LocalDate.now());
    transaction.setAmount(BigDecimal.valueOf(100));
    List<Transaction> mockTransactions = Collections.singletonList(transaction);
    User mockUser = new User(testEmail, "testPassword");
    mockUser.setId(1L);
    when(userService.getUserByEmail(testEmail)).thenReturn(mockUser);
    when(transactionService.importMultipleCSVs(eq(1L), any(MultipartFile.class)))
        .thenReturn(mockTransactions);

    MockMultipartFile file =
        new MockMultipartFile("files", "test.csv", "text/csv", "sample content".getBytes());

    // Act & Assert
    mockMvc
        .perform(
            multipart("/api/transactions/upload")
                .file(file)
                .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
        .andExpect(content().string("All CSVs imported successfully"));

    verify(transactionService, times(1)).importMultipleCSVs(eq(1L), any(MultipartFile.class));
    verify(userService, times(1)).getUserByEmail(eq(testEmail));
  }
}
