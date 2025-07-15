package com.shoxys.budgetbuddy_backend.Controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shoxys.budgetbuddy_backend.DTOs.DashboardResponse;
import com.shoxys.budgetbuddy_backend.Entities.User;
import com.shoxys.budgetbuddy_backend.Security.AppUserDetails;
import com.shoxys.budgetbuddy_backend.Services.DashboardService;
import com.shoxys.budgetbuddy_backend.Services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        value = DashboardController.class,
        excludeFilters = @Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = com.shoxys.budgetbuddy_backend.Security.JwtAuthFilter.class
        )
)
@AutoConfigureMockMvc(addFilters = false)
public class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DashboardService dashboardService;

    @MockitoBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private final String testEmail = "test@example.com";
    private final long testUserId = 1L;

    @BeforeEach
    void setUp() {
        // Setup user and AppUserDetails
        User user = new User();
        user.setId(testUserId);
        user.setEmail(testEmail);

        AppUserDetails principal = new AppUserDetails(user);

        // Set up the security context
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));
        SecurityContextHolder.setContext(context);

        // Mock UserService
        when(userService.getUserIdByEmail(eq(testEmail))).thenReturn(testUserId);

        // Mock DashboardService
        DashboardResponse partialResponse = new DashboardResponse(
                BigDecimal.valueOf(1000),
                List.of(),
                null, null, null, null, null, null, null
        );
        when(dashboardService.getDashboardResponse(eq(testUserId))).thenReturn(partialResponse);
    }

    @Test
    void testGetDashboardData_Returns200AndPartialResponse() throws Exception {
        mockMvc.perform(get("/api/dashboard/data")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.totalBalance").value(1000))
                .andExpect(jsonPath("$.accountSummaries").isArray())
                .andExpect(jsonPath("$.accountSummaries").isEmpty());
    }
}