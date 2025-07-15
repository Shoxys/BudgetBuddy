package com.shoxys.budgetbuddy_backend.Services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.password4j.Password;
import com.shoxys.budgetbuddy_backend.DTOs.AuthResponse;
import com.shoxys.budgetbuddy_backend.DTOs.LoginRequest;
import com.shoxys.budgetbuddy_backend.DTOs.RegisterRequest;
import com.shoxys.budgetbuddy_backend.Entities.User;
import com.shoxys.budgetbuddy_backend.Exceptions.EmailExistsException;
import com.shoxys.budgetbuddy_backend.Exceptions.UserNotFoundException;
import com.shoxys.budgetbuddy_backend.Repo.UserRepo;
import com.shoxys.budgetbuddy_backend.Security.AppUserDetails;
import com.shoxys.budgetbuddy_backend.Security.JwtUtil;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

  @Mock private UserRepo userRepo;
  @Mock private AuthenticationManager authManager;
  @Mock private JwtUtil jwtUtil;

  @InjectMocks private AuthService authService;

  static class TestData {
    static final String VALID_EMAIL = "valid@example.com";
    static final String VALID_PASSWORD = "PlainUserPass";
    static final String HASHED_PASSWORD =
        Password.hash(VALID_PASSWORD).addRandomSalt().withScrypt().getResult();
    static final String INVALID_EMAIL = "invalid@example.com";
    static final String JWT_TOKEN = "mocked.jwt.token";

    static final User VALID_USER = new User(VALID_EMAIL, HASHED_PASSWORD);
    static final LoginRequest VALID_LOGIN = new LoginRequest(VALID_EMAIL, VALID_PASSWORD);
    static final LoginRequest INVALID_LOGIN = new LoginRequest(INVALID_EMAIL, VALID_PASSWORD);
    static final RegisterRequest REGISTER_REQUEST =
        new RegisterRequest(VALID_EMAIL, VALID_PASSWORD);
  }

  @Test
  void authenticate_shouldReturnAuthResponse() {
    // Arrange
    var token =
        new UsernamePasswordAuthenticationToken(TestData.VALID_EMAIL, TestData.VALID_PASSWORD);
    when(authManager.authenticate(token)).thenReturn(token);
    when(userRepo.findByEmail(TestData.VALID_EMAIL)).thenReturn(Optional.of(TestData.VALID_USER));
    when(jwtUtil.generateToken(any(AppUserDetails.class))).thenReturn(TestData.JWT_TOKEN);

    // Act
    AuthResponse result = authService.authenticate(TestData.VALID_LOGIN);

    // Assert
    assertEquals(TestData.JWT_TOKEN, result.getToken());
    assertEquals("User authenticated successfully", result.getMessage());
  }

  @Test
  void authenticate_shouldThrowIfUserNotFound() {
    var token =
        new UsernamePasswordAuthenticationToken(TestData.INVALID_EMAIL, TestData.VALID_PASSWORD);
    when(authManager.authenticate(token)).thenReturn(token);
    when(userRepo.findByEmail(TestData.INVALID_EMAIL)).thenReturn(Optional.empty());

    assertThrows(
        UserNotFoundException.class, () -> authService.authenticate(TestData.INVALID_LOGIN));
  }

  @Test
  void register_shouldCreateUserIfNotExists() {
    when(userRepo.existsByEmail(TestData.REGISTER_REQUEST.getEmail())).thenReturn(false);
    when(userRepo.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

    assertDoesNotThrow(() -> authService.register(TestData.REGISTER_REQUEST));
    verify(userRepo).save(any(User.class));
  }

  @Test
  void register_shouldThrowIfEmailAlreadyExists() {
    when(userRepo.existsByEmail(TestData.REGISTER_REQUEST.getEmail())).thenReturn(true);

    assertThrows(EmailExistsException.class, () -> authService.register(TestData.REGISTER_REQUEST));
  }

  @Test
  void login_shouldPassIfCredentialsCorrect() {
    User user = new User(TestData.VALID_EMAIL, TestData.HASHED_PASSWORD);
    when(userRepo.findByEmail(TestData.VALID_EMAIL)).thenReturn(Optional.of(user));

    assertDoesNotThrow(() -> authService.login(TestData.VALID_LOGIN));
  }

  @Test
  void login_shouldThrowIfUserNotFound() {
    when(userRepo.findByEmail(TestData.VALID_EMAIL)).thenReturn(Optional.empty());

    assertThrows(IllegalArgumentException.class, () -> authService.login(TestData.VALID_LOGIN));
  }

  @Test
  void login_shouldThrowIfPasswordInvalid() {
    User user = new User(TestData.VALID_EMAIL, TestData.HASHED_PASSWORD);
    when(userRepo.findByEmail(TestData.VALID_EMAIL)).thenReturn(Optional.of(user));

    LoginRequest badLogin = new LoginRequest(TestData.VALID_EMAIL, "wrongpassword");

    assertThrows(IllegalArgumentException.class, () -> authService.login(badLogin));
  }

  @Test
  void hashPassword_shouldReturnHashedPassword() {
    String hashed = authService.hashPassword("mypassword");

    assertNotNull(hashed);
    assertNotEquals("mypassword", hashed);
  }

  @Test
  void verifyPassword_shouldReturnTrueForCorrectPassword() {
    String hashed = authService.hashPassword(TestData.VALID_PASSWORD);

    boolean result = authService.verifyPassword(TestData.VALID_PASSWORD, hashed);
    assertTrue(result);
  }

  @Test
  void verifyPassword_shouldReturnFalseForIncorrectPassword() {
    String hashed = authService.hashPassword("correctpass");

    boolean result = authService.verifyPassword("wrongpass", hashed);
    assertFalse(result);
  }
}
