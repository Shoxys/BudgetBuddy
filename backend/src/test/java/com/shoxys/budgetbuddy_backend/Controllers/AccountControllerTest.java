package com.shoxys.budgetbuddy_backend.Controllers;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shoxys.budgetbuddy_backend.DTOs.Account.UpdateAccountRequest;
import com.shoxys.budgetbuddy_backend.Entities.Account;
import com.shoxys.budgetbuddy_backend.Entities.User;
import com.shoxys.budgetbuddy_backend.Enums.AccountType;
import com.shoxys.budgetbuddy_backend.Exceptions.UserNotFoundException;
import com.shoxys.budgetbuddy_backend.Security.AppUserDetails;
import com.shoxys.budgetbuddy_backend.Services.AccountService;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(
    value = AccountController.class,
    excludeFilters = {
      @ComponentScan.Filter(
          type = FilterType.ASSIGNABLE_TYPE,
          classes = com.shoxys.budgetbuddy_backend.Security.JwtAuthFilter.class)
    })
@AutoConfigureMockMvc(addFilters = false)
class AccountControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private AccountService accountService;

  @Autowired private ObjectMapper objectMapper;

  private User mockUser;
  private AppUserDetails principal;

  @BeforeEach
  void setUp() {
    mockUser = new User();
    mockUser.setEmail("test@example.com");

    principal = new AppUserDetails(mockUser);
    SecurityContext context = SecurityContextHolder.createEmptyContext();
    context.setAuthentication(
        new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));
    SecurityContextHolder.setContext(context);
  }

  @Test
  void testUpdateAccountWithId_success() throws Exception {
    // Arrange
    UpdateAccountRequest request = new UpdateAccountRequest();
    request.setId(1L);
    request.setName("My Savings");
    request.setAccountType(AccountType.SAVINGS);
    request.setBalance(BigDecimal.valueOf(15000));

    Account mockAccount = new Account();
    mockAccount.setId(1L);
    mockAccount.setName("My Savings");
    mockAccount.setType(AccountType.SAVINGS);
    mockAccount.setBalance(BigDecimal.valueOf(15000));

    when(accountService.upsertAccountBalance(
            eq("test@example.com"),
            eq(1L),
            eq("My Savings"),
            eq(AccountType.SAVINGS),
            eq(BigDecimal.valueOf(15000))))
        .thenReturn(mockAccount);

    // Act & Assert
    mockMvc
        .perform(
            post("/api/account/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(content().string("Account updated successfully"));

    verify(accountService, times(1))
        .upsertAccountBalance(
            eq("test@example.com"),
            eq(1L),
            eq("My Savings"),
            eq(AccountType.SAVINGS),
            eq(BigDecimal.valueOf(15000)));
  }

  @Test
  void testUpdateAccountWithoutId_success() throws Exception {
    // Arrange
    UpdateAccountRequest request = new UpdateAccountRequest();
    request.setName("New Investments");
    request.setAccountType(AccountType.INVESTMENTS);
    request.setBalance(BigDecimal.valueOf(8000));

    Account mockAccount = new Account();
    mockAccount.setName("New Investments");
    mockAccount.setType(AccountType.INVESTMENTS);
    mockAccount.setBalance(BigDecimal.valueOf(8000));

    when(accountService.upsertAccountBalance(
            eq("test@example.com"),
            eq(null),
            eq("New Investments"),
            eq(AccountType.INVESTMENTS),
            eq(BigDecimal.valueOf(8000))))
        .thenReturn(mockAccount);

    // Act & Assert
    mockMvc
        .perform(
            post("/api/account/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(content().string("Account updated successfully"));

    verify(accountService, times(1))
        .upsertAccountBalance(
            eq("test@example.com"),
            eq(null),
            eq("New Investments"),
            eq(AccountType.INVESTMENTS),
            eq(BigDecimal.valueOf(8000)));
  }

  @Test
  void testUpdateAccount_invalidRequest_nullName() throws Exception {
    // Arrange
    UpdateAccountRequest request = new UpdateAccountRequest();
    request.setId(1L);
    request.setName(null); // Invalid: null name
    request.setAccountType(AccountType.SAVINGS);
    request.setBalance(BigDecimal.valueOf(15000));

    when(accountService.upsertAccountBalance(
            eq("test@example.com"),
            eq(1L),
            eq(null),
            eq(AccountType.SAVINGS),
            eq(BigDecimal.valueOf(15000))))
        .thenThrow(new IllegalArgumentException("Name cannot be empty"));

    // Act & Assert
    mockMvc
        .perform(
            post("/api/account/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());

    verify(accountService, times(1))
        .upsertAccountBalance(
            eq("test@example.com"),
            eq(1L),
            eq(null),
            eq(AccountType.SAVINGS),
            eq(BigDecimal.valueOf(15000)));
  }

  @Test
  void testUpdateAccount_invalidRequest_emptyName() throws Exception {
    // Arrange
    UpdateAccountRequest request = new UpdateAccountRequest();
    request.setId(1L);
    request.setName(""); // Invalid: empty name
    request.setAccountType(AccountType.SAVINGS);
    request.setBalance(BigDecimal.valueOf(15000));

    when(accountService.upsertAccountBalance(
            eq("test@example.com"),
            eq(1L),
            eq(""),
            eq(AccountType.SAVINGS),
            eq(BigDecimal.valueOf(15000))))
        .thenThrow(new IllegalArgumentException("Name cannot be empty"));

    // Act & Assert
    mockMvc
        .perform(
            post("/api/account/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());

    verify(accountService, times(1))
        .upsertAccountBalance(
            eq("test@example.com"),
            eq(1L),
            eq(""),
            eq(AccountType.SAVINGS),
            eq(BigDecimal.valueOf(15000)));
  }

  @Test
  void testUpdateAccount_invalidRequest_nullType() throws Exception {
    // Arrange
    UpdateAccountRequest request = new UpdateAccountRequest();
    request.setId(1L);
    request.setName("My Savings");
    request.setAccountType(null); // Invalid: null type
    request.setBalance(BigDecimal.valueOf(15000));

    when(accountService.upsertAccountBalance(
            eq("test@example.com"),
            eq(1L),
            eq("My Savings"),
            eq(null),
            eq(BigDecimal.valueOf(15000))))
        .thenThrow(new IllegalArgumentException("Type cannot be empty"));

    // Act & Assert
    mockMvc
        .perform(
            post("/api/account/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());

    verify(accountService, times(1))
        .upsertAccountBalance(
            eq("test@example.com"),
            eq(1L),
            eq("My Savings"),
            eq(null),
            eq(BigDecimal.valueOf(15000)));
  }

  @Test
  void testUpdateAccount_invalidRequest_nullBalance() throws Exception {
    // Arrange
    UpdateAccountRequest request = new UpdateAccountRequest();
    request.setId(1L);
    request.setName("My Savings");
    request.setAccountType(AccountType.SAVINGS);
    request.setBalance(null); // Invalid: null balance

    when(accountService.upsertAccountBalance(
            eq("test@example.com"), eq(1L), eq("My Savings"), eq(AccountType.SAVINGS), eq(null)))
        .thenThrow(new IllegalArgumentException("New balance cannot be null"));

    // Act & Assert
    mockMvc
        .perform(
            post("/api/account/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());

    verify(accountService, times(1))
        .upsertAccountBalance(
            eq("test@example.com"), eq(1L), eq("My Savings"), eq(AccountType.SAVINGS), eq(null));
  }

  @Test
  void testUpdateAccount_userNotFound() throws Exception {
    // Arrange
    UpdateAccountRequest request = new UpdateAccountRequest();
    request.setId(1L);
    request.setName("My Savings");
    request.setAccountType(AccountType.SAVINGS);
    request.setBalance(BigDecimal.valueOf(15000));

    when(accountService.upsertAccountBalance(
            eq("test@example.com"),
            eq(1L),
            eq("My Savings"),
            eq(AccountType.SAVINGS),
            eq(BigDecimal.valueOf(15000))))
        .thenThrow(new UserNotFoundException("User not found"));

    // Act & Assert
    mockMvc
        .perform(
            post("/api/account/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNotFound());

    verify(accountService, times(1))
        .upsertAccountBalance(
            eq("test@example.com"),
            eq(1L),
            eq("My Savings"),
            eq(AccountType.SAVINGS),
            eq(BigDecimal.valueOf(15000)));
  }

  @Test
  void testUpdateAccount_unauthenticated() throws Exception {
    // Arrange
    SecurityContextHolder.clearContext(); // Simulate no authentication
    UpdateAccountRequest request = new UpdateAccountRequest();
    request.setId(1L);
    request.setName("My Savings");
    request.setAccountType(AccountType.SAVINGS);
    request.setBalance(BigDecimal.valueOf(15000));

    // Act & Assert
    mockMvc
        .perform(
            post("/api/account/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isUnauthorized());

    verify(accountService, never()).upsertAccountBalance(any(), any(), any(), any(), any());
  }

  @Test
  void testGetAccountBalance_success() throws Exception {
    // Arrange
    String name = "Savings Account";
    AccountType accountType = AccountType.SAVINGS;
    BigDecimal balance = BigDecimal.valueOf(2500);

    when(accountService.getAccountBalance(eq("test@example.com"), eq(name), eq(accountType)))
        .thenReturn(balance);

    // Act & Assert
    mockMvc
        .perform(
            get("/api/account/balance")
                .param("name", name)
                .param("accountType", accountType.name())
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().json("2500"));

    verify(accountService, times(1))
        .getAccountBalance(eq("test@example.com"), eq(name), eq(accountType));
  }

  @Test
  void testGetAccountBalance_accountNotFound() throws Exception {
    // Arrange
    String name = "Savings Account";
    AccountType type = AccountType.SAVINGS;

    when(accountService.getAccountBalance(eq("test@example.com"), eq(name), eq(type)))
        .thenReturn(null);

    // Act & Assert
    mockMvc
        .perform(
            get("/api/account/balance")
                .param("name", name)
                .param("accountType", type.name())
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().json("0"));

    verify(accountService, times(1)).getAccountBalance(eq("test@example.com"), eq(name), eq(type));
  }

  @Test
  void testGetAccountBalance_userNotFound() throws Exception {
    // Arrange
    String name = "Savings Account";
    AccountType type = AccountType.SAVINGS;

    when(accountService.getAccountBalance(eq("test@example.com"), eq(name), eq(type)))
        .thenThrow(new UserNotFoundException("User not found"));

    // Act & Assert
    mockMvc
        .perform(
            get("/api/account/balance")
                .param("name", name)
                .param("accountType", type.name())
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());

    verify(accountService, times(1)).getAccountBalance(eq("test@example.com"), eq(name), eq(type));
  }

  @Test
  void testGetAccountBalance_unauthenticated() throws Exception {
    // Arrange
    SecurityContextHolder.clearContext(); // Simulate no authentication
    String name = "Savings Account";
    AccountType type = AccountType.SAVINGS;

    // Act & Assert
    mockMvc
        .perform(
            get("/api/account/balance")
                .param("name", name)
                .param("accountType", type.name())
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized());

    verify(accountService, never()).getAccountBalance(any(), any(), any());
  }
}
