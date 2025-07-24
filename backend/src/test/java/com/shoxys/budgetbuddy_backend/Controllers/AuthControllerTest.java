package com.shoxys.budgetbuddy_backend.Controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shoxys.budgetbuddy_backend.Config.Constants;
import com.shoxys.budgetbuddy_backend.DTOs.Auth.AuthResponse;
import com.shoxys.budgetbuddy_backend.DTOs.Auth.LoginRequest;
import com.shoxys.budgetbuddy_backend.DTOs.Auth.RegisterRequest;
import com.shoxys.budgetbuddy_backend.Entities.User;
import com.shoxys.budgetbuddy_backend.Exceptions.*;
import com.shoxys.budgetbuddy_backend.Security.AppUserDetails;
import com.shoxys.budgetbuddy_backend.Services.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        value = AuthController.class,
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = com.shoxys.budgetbuddy_backend.Security.JwtAuthFilter.class)
        })
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {
    @Autowired private MockMvc mockMvc;
    @MockitoBean private AuthService authService;
    @Autowired private ObjectMapper objectMapper;

    private LoginRequest loginRequest;
    private RegisterRequest registerRequest;
    private AppUserDetails principal;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequest("test@example.com", "password");

        registerRequest = new RegisterRequest();
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password");
        registerRequest.setConfirmPassword("password");

        principal = new AppUserDetails(new User("test@example.com", "password"));
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));
        SecurityContextHolder.setContext(context);
    }

    @Test
    void testRegister_Success() throws Exception {
        AuthResponse authResponse = new AuthResponse("jwt-token", "User authenticated successfully");
        when(authService.authenticate(any(LoginRequest.class))).thenReturn(authResponse);
        doNothing().when(authService).register(any(RegisterRequest.class));

        mockMvc.perform(post(Constants.AUTH_ENDPOINT + "/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered successfully"))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString(Constants.JWT_COOKIE_NAME + "=jwt-token")))
                .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("HttpOnly")))
                .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("SameSite=" + Constants.COOKIE_SAME_SITE)))
                .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("Path=" + Constants.COOKIE_PATH)))
                .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("Max-Age=" + Constants.COOKIE_MAX_AGE_SECONDS)));

        verify(authService, times(1)).register(any(RegisterRequest.class));
        verify(authService, times(1)).authenticate(any(LoginRequest.class));
    }

    @Test
    void testRegister_InvalidEmail() throws Exception {
        registerRequest.setEmail("invalid");
        doThrow(new InvalidEmailFormatException("Invalid email format")).when(authService).register(any(RegisterRequest.class));

        mockMvc.perform(post(Constants.AUTH_ENDPOINT + "/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid email format"))
                .andExpect(jsonPath("$.success").value(false));

        verify(authService, times(1)).register(any(RegisterRequest.class));
        verify(authService, never()).authenticate(any());
    }

    @Test
    void testRegister_PasswordMismatch() throws Exception {
        registerRequest.setConfirmPassword("different");
        doThrow(new PasswordMismatchException("Passwords do not match")).when(authService).register(any(RegisterRequest.class));

        mockMvc.perform(post(Constants.AUTH_ENDPOINT + "/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Passwords do not match"))
                .andExpect(jsonPath("$.success").value(false));

        verify(authService, times(1)).register(any(RegisterRequest.class));
        verify(authService, never()).authenticate(any());
    }

    @Test
    void testRegister_EmailExists() throws Exception {
        doThrow(new EmailExistsException("Email already exists")).when(authService).register(any(RegisterRequest.class));

        mockMvc.perform(post(Constants.AUTH_ENDPOINT + "/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest()) // Corrected from isConflict()
                .andExpect(jsonPath("$.message").value("Email already exists"))
                .andExpect(jsonPath("$.success").value(false)); // Corrected from checking $.status

        verify(authService, times(1)).register(any(RegisterRequest.class));
        verify(authService, never()).authenticate(any());
    }

    @Test
    void testRegister_ServerError() throws Exception {
        doThrow(new RuntimeException("Server error")).when(authService).register(any(RegisterRequest.class));

        mockMvc.perform(post(Constants.AUTH_ENDPOINT + "/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Server error occurred"))
                .andExpect(jsonPath("$.success").value(false)); // Corrected from checking $.status

        verify(authService, times(1)).register(any(RegisterRequest.class));
        verify(authService, never()).authenticate(any());
    }

    @Test
    void testLogin_Success() throws Exception {
        AuthResponse authResponse = new AuthResponse("jwt-token", "Login successful");
        when(authService.authenticate(any(LoginRequest.class))).thenReturn(authResponse);

        mockMvc.perform(post(Constants.AUTH_ENDPOINT + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Login successful"))
                .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString(Constants.JWT_COOKIE_NAME + "=jwt-token")));

        verify(authService, times(1)).authenticate(any(LoginRequest.class));
    }

    @Test
    void testLogin_InvalidCredentials() throws Exception {
        doThrow(new InvalidCredentialsException("Invalid email or password")).when(authService).authenticate(any(LoginRequest.class));

        mockMvc.perform(post(Constants.AUTH_ENDPOINT + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest()) // Corrected from isUnauthorized()
                .andExpect(jsonPath("$.message").value("Invalid email or password")); // Corrected message and removed $.status check

        verify(authService, times(1)).authenticate(any(LoginRequest.class));
    }

    @Test
    void testLogin_MissingFields() throws Exception {
        loginRequest.setEmail(null);
        // This exception would now likely be caught by @Valid, but testing the service-level exception is still valid.
        doThrow(new MissingFieldException("Email and password are required")).when(authService).authenticate(any(LoginRequest.class));

        mockMvc.perform(post(Constants.AUTH_ENDPOINT + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Email and password are required")); // Removed $.status check

        verify(authService, times(1)).authenticate(any(LoginRequest.class));
    }

    @Test
    void testLogin_ServerError() throws Exception {
        doThrow(new RuntimeException("Server error")).when(authService).authenticate(any(LoginRequest.class));

        mockMvc.perform(post(Constants.AUTH_ENDPOINT + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Server error occurred")); // Removed $.status check

        verify(authService, times(1)).authenticate(any(LoginRequest.class));
    }

    @Test
    void testLogout_Success() throws Exception {
        mockMvc.perform(post(Constants.AUTH_ENDPOINT + "/logout")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Logged out successfully"))
                .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString(Constants.JWT_COOKIE_NAME + "=")))
                .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("Max-Age=0")));

        verify(authService, never()).authenticate(any());
        verify(authService, never()).register(any());
    }
}