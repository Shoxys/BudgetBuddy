package com.shoxys.budgetbuddy_backend.Controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shoxys.budgetbuddy_backend.DTOs.UpdateAccountRequest;
import com.shoxys.budgetbuddy_backend.Entities.Account;
import com.shoxys.budgetbuddy_backend.Entities.User;
import com.shoxys.budgetbuddy_backend.Enums.AccountType;
import com.shoxys.budgetbuddy_backend.Security.AppUserDetails;
import com.shoxys.budgetbuddy_backend.Services.AccountService;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.math.BigDecimal;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
    value = AccountController.class,
    excludeFilters = {
        @ComponentScan.Filter(
            type = FilterType.ASSIGNABLE_TYPE,
            classes = com.shoxys.budgetbuddy_backend.Security.JwtAuthFilter.class
        )
    }
)
@AutoConfigureMockMvc(addFilters = false)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AccountService accountService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        User mockUser = new User();
        mockUser.setEmail("test@example.com");

        // Setup security context with AppUserDetails
        AppUserDetails principal = new AppUserDetails(mockUser);
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));
        SecurityContextHolder.setContext(context);
    }

    @Test
    void testUpdateSavings() throws Exception {
        // Arrange
        UpdateAccountRequest request = new UpdateAccountRequest(
                "My Savings",
                AccountType.SAVINGS,
                BigDecimal.valueOf(10000)
        );

        Account mockAccount = new Account();
        mockAccount.setName("My Savings");
        mockAccount.setType(AccountType.SAVINGS);
        mockAccount.setBalance(BigDecimal.valueOf(10000));
        mockAccount.setManual(true);

        when(accountService.upsertAccountBalance(
                eq("test@example.com"),
                eq("My Savings"),
                eq(AccountType.SAVINGS),
                eq(BigDecimal.valueOf(10000))
        )).thenReturn(mockAccount);

        // Act & Assert
        mockMvc.perform(post("/api/account/update-savings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(accountService, times(1)).upsertAccountBalance(
                eq("test@example.com"),
                eq("My Savings"),
                eq(AccountType.SAVINGS),
                eq(BigDecimal.valueOf(10000))
        );
    }


    @Test
    @WithMockUser(username = "test@example.com")
    void testUpdateInvestments() throws Exception {
        UpdateAccountRequest request = new UpdateAccountRequest(
                "Stocks",
                AccountType.INVESTMENTS,
                BigDecimal.valueOf(10000)
        );

        Account mockAccount = new Account();
        mockAccount.setName("Stocks");
        mockAccount.setType(AccountType.INVESTMENTS);
        mockAccount.setBalance(BigDecimal.valueOf(5000));
        mockAccount.setManual(true);

        when(accountService.upsertAccountBalance(
                eq("test@example.com"),
                eq("Stocks"),
                eq(AccountType.INVESTMENTS),
                eq(BigDecimal.valueOf(10000))
        )).thenReturn(mockAccount);

        mockMvc.perform(post("/api/account/update-investments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(accountService, times(1)).upsertAccountBalance(
                eq("test@example.com"),
                eq("Stocks"),
                eq(AccountType.INVESTMENTS),
                eq(BigDecimal.valueOf(10000))
        );
    }
}