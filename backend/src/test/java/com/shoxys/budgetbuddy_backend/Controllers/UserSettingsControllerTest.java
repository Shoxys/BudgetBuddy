package com.shoxys.budgetbuddy_backend.Controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shoxys.budgetbuddy_backend.DTOs.AuthResponse;
import com.shoxys.budgetbuddy_backend.DTOs.ChangePasswordRequest;
import com.shoxys.budgetbuddy_backend.DTOs.UpdateEmailRequest;
import com.shoxys.budgetbuddy_backend.Entities.User;
import com.shoxys.budgetbuddy_backend.Security.AppUserDetails;
import com.shoxys.budgetbuddy_backend.Services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
    value = UserSettingsController.class,
    excludeFilters = {
        @ComponentScan.Filter(
            type = FilterType.ASSIGNABLE_TYPE,
            classes = com.shoxys.budgetbuddy_backend.Security.JwtAuthFilter.class
        )
    }
)
@AutoConfigureMockMvc(addFilters = false)
public class UserSettingsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;



    @Autowired
    private ObjectMapper objectMapper;

    private final String testEmail = "test@example.com";
    private ChangePasswordRequest passwordRequest;
    private UpdateEmailRequest emailRequest;

    @BeforeEach
    void setUp() {
        // Setup security context with AppUserDetails
        User user = new User();
        user.setEmail(testEmail);
        AppUserDetails principal = new AppUserDetails(user);

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));
        SecurityContextHolder.setContext(context);

        // Common request objects
        passwordRequest = new ChangePasswordRequest("oldpass", "newpass", "newpass");
        emailRequest = new UpdateEmailRequest("current@example.com", "new@example.com", "password123");
    }

    @Test
    void testChangePassword() throws Exception {
        mockMvc.perform(put("/api/settings/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passwordRequest)))
                .andExpect(status().isOk());

        verify(userService, times(1)).changePassword(eq("test@example.com"), any(ChangePasswordRequest.class));

    }

    @Test
    void testUpdateEmail() throws Exception {
        AuthResponse mockResponse = new AuthResponse("mock-token", "Updated Successfully");

        when(userService.updateEmail(eq("test@example.com"), any(UpdateEmailRequest.class)))
                .thenReturn(mockResponse);

        mockMvc.perform(put("/api/settings/update-email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emailRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(mockResponse)));

        verify(userService, times(1)).updateEmail(eq("test@example.com"), any(UpdateEmailRequest.class));
    }

    @Test
    void testDeleteAccount() throws Exception {
        mockMvc.perform(delete("/api/settings/delete-account"))
                .andExpect(status().isOk());

        verify(userService, times(1)).deleteAccount(eq(testEmail));
    }
}
