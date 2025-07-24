package com.shoxys.budgetbuddy_backend.Services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.shoxys.budgetbuddy_backend.DTOs.Auth.AuthResponse;
import com.shoxys.budgetbuddy_backend.DTOs.Auth.LoginRequest;
import com.shoxys.budgetbuddy_backend.DTOs.Auth.RegisterRequest;
import com.shoxys.budgetbuddy_backend.Entities.User;
import com.shoxys.budgetbuddy_backend.Exceptions.*;
import com.shoxys.budgetbuddy_backend.Repo.UserRepo;
import com.shoxys.budgetbuddy_backend.Security.AppUserDetails;
import com.shoxys.budgetbuddy_backend.Security.JwtUtil;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

class AuthServiceTest {
  @Mock private UserRepo userRepo;
  @Mock private AuthenticationManager authenticationManager;
  @Mock private JwtUtil jwtUtil;
  @Mock private PasswordEncoder passwordEncoder;
  @InjectMocks private AuthService authService;

  private User user;
  private LoginRequest loginRequest;
  private RegisterRequest registerRequest;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    user = new User("test@example.com", "encoded-password");
    user.setId(1L);
    loginRequest = new LoginRequest();
    loginRequest.setEmail("test@example.com");
    loginRequest.setPassword("password");
    registerRequest = new RegisterRequest();
    registerRequest.setEmail("test@example.com");
    registerRequest.setPassword("password");
    registerRequest.setConfirmPassword("password");
  }

  @Test
  void testAuthenticate_Success() {
    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
        .thenReturn(new UsernamePasswordAuthenticationToken("test@example.com", null));
    when(userRepo.findByEmail("test@example.com")).thenReturn(Optional.of(user));
    when(jwtUtil.generateToken(any(AppUserDetails.class))).thenReturn("jwt-token");
    AuthResponse response = authService.authenticate(loginRequest);
    assertEquals("jwt-token", response.getToken());
    verify(authenticationManager, times(1)).authenticate(any());
    verify(jwtUtil, times(1)).generateToken(any());
  }

  @Test
  void testAuthenticate_InvalidEmail() {
    loginRequest.setEmail("invalid");
    assertThrows(InvalidEmailFormatException.class, () -> authService.authenticate(loginRequest));
    verify(authenticationManager, never()).authenticate(any());
  }

  @Test
  void testAuthenticate_BadCredentials() {
    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
        .thenThrow(new BadCredentialsException("Invalid credentials"));
    assertThrows(InvalidCredentialsException.class, () -> authService.authenticate(loginRequest));
    verify(authenticationManager, times(1)).authenticate(any());
  }

  @Test
  void testRegister_Success() {
    when(userRepo.existsByEmail("test@example.com")).thenReturn(false);
    when(passwordEncoder.encode("password")).thenReturn("encoded-password");
    when(userRepo.save(any(User.class))).thenReturn(user);
    authService.register(registerRequest);
    verify(userRepo, times(1)).save(any(User.class));
  }

  @Test
  void testRegister_EmailExists() {
    when(userRepo.existsByEmail("test@example.com")).thenReturn(true);
    assertThrows(EmailExistsException.class, () -> authService.register(registerRequest));
    verify(userRepo, never()).save(any());
  }

  @Test
  void testRegister_PasswordMismatch() {
    registerRequest.setConfirmPassword("different");
    assertThrows(PasswordMismatchException.class, () -> authService.register(registerRequest));
    verify(userRepo, never()).save(any());
  }
}
