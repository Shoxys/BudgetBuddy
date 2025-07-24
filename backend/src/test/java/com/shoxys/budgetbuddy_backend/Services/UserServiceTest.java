package com.shoxys.budgetbuddy_backend.Services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.shoxys.budgetbuddy_backend.DTOs.Auth.AuthResponse;
import com.shoxys.budgetbuddy_backend.DTOs.Auth.ChangePasswordRequest;
import com.shoxys.budgetbuddy_backend.DTOs.Auth.UpdateEmailRequest;
import com.shoxys.budgetbuddy_backend.Entities.User;
import com.shoxys.budgetbuddy_backend.Exceptions.EmailExistsException;
import com.shoxys.budgetbuddy_backend.Exceptions.UserNotFoundException;
import com.shoxys.budgetbuddy_backend.Repo.UserRepo;
import com.shoxys.budgetbuddy_backend.Security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock private UserRepo userRepo;
  @Mock private PasswordEncoder passwordEncoder;
  @Mock private JwtUtil jwtUtil;

  @InjectMocks private UserService userService;

  private static final String VALID_EMAIL = "valid@example.com";
  private static final String NEW_EMAIL = "new@example.com";
  private static final String PASSWORD = "password123";
  private static final String HASHED_PASSWORD = "hashedPass123";

  private User mockUser;

  @BeforeEach
  void setup() {
    mockUser = new User(VALID_EMAIL, HASHED_PASSWORD);
  }

  @Test
  void getUserIdByEmail_shouldReturnUserId() {
    when(userRepo.getUserIdByEmail(VALID_EMAIL)).thenReturn(1L);

    long userId = userService.getUserIdByEmail(VALID_EMAIL);

    assertEquals(1L, userId);
  }

  @Test
  void changePassword_shouldUpdatePasswordSuccessfully() {
    ChangePasswordRequest request = new ChangePasswordRequest(PASSWORD, "newPass", "newPass");

    when(userRepo.getUserByEmail(VALID_EMAIL)).thenReturn(Optional.of(mockUser));
    when(passwordEncoder.matches(PASSWORD, HASHED_PASSWORD)).thenReturn(true);
    when(passwordEncoder.encode("newPass")).thenReturn("newHashed");

    userService.changePassword(VALID_EMAIL, request);

    assertEquals("newHashed", mockUser.getHashedPassword());
    verify(userRepo).save(mockUser);
  }

  @Test
  void changePassword_shouldThrowIfCurrentPasswordIncorrect() {
    ChangePasswordRequest request = new ChangePasswordRequest("wrongPass", "newPass", "newPass");

    when(userRepo.getUserByEmail(VALID_EMAIL)).thenReturn(Optional.of(mockUser));
    when(passwordEncoder.matches("wrongPass", HASHED_PASSWORD)).thenReturn(false);

    assertThrows(SecurityException.class, () -> userService.changePassword(VALID_EMAIL, request));
  }

  @Test
  void changePassword_shouldThrowIfPasswordsDoNotMatch() {
    ChangePasswordRequest request = new ChangePasswordRequest(PASSWORD, "newPass1", "newPass2");

    when(userRepo.getUserByEmail(VALID_EMAIL)).thenReturn(Optional.of(mockUser));
    when(passwordEncoder.matches(PASSWORD, HASHED_PASSWORD)).thenReturn(true);

    assertThrows(SecurityException.class, () -> userService.changePassword(VALID_EMAIL, request));
  }

  @Test
  void updateEmail_shouldUpdateEmailAndReturnNewToken() {
    UpdateEmailRequest request = new UpdateEmailRequest(VALID_EMAIL, NEW_EMAIL, PASSWORD);

    when(userRepo.getUserByEmail(VALID_EMAIL)).thenReturn(Optional.of(mockUser));
    when(passwordEncoder.matches(PASSWORD, HASHED_PASSWORD)).thenReturn(true);
    when(jwtUtil.generateToken(any())).thenReturn("jwtToken");

    AuthResponse response = userService.updateEmail(VALID_EMAIL, request);

    assertEquals(NEW_EMAIL, mockUser.getEmail());
    assertEquals("jwtToken", response.getToken());
    assertEquals("Email updated successfully", response.getMessage());
    verify(userRepo).save(mockUser);
  }

  @Test
  void updateEmail_shouldThrowIfCurrentEmailMismatch() {
    UpdateEmailRequest request = new UpdateEmailRequest("wrong@example.com", NEW_EMAIL, PASSWORD);

    when(userRepo.getUserByEmail(VALID_EMAIL)).thenReturn(Optional.of(mockUser));

    assertThrows(SecurityException.class, () -> userService.updateEmail(VALID_EMAIL, request));
  }

  @Test
  void updateEmail_shouldThrowIfNewEmailIsSame() {
    UpdateEmailRequest request = new UpdateEmailRequest(VALID_EMAIL, VALID_EMAIL, PASSWORD);

    when(userRepo.getUserByEmail(VALID_EMAIL)).thenReturn(Optional.of(mockUser));
    when(passwordEncoder.matches(PASSWORD, HASHED_PASSWORD)).thenReturn(true);

    assertThrows(EmailExistsException.class, () -> userService.updateEmail(VALID_EMAIL, request));
  }

  @Test
  void deleteAccount_shouldDeleteUser() {
    when(userRepo.getUserByEmail(VALID_EMAIL)).thenReturn(Optional.of(mockUser));

    userService.deleteAccount(VALID_EMAIL);

    verify(userRepo).delete(mockUser);
  }

  @Test
  void deleteAccount_shouldThrowIfUserNotFound() {
    when(userRepo.getUserByEmail(VALID_EMAIL)).thenReturn(Optional.empty());

    assertThrows(UserNotFoundException.class, () -> userService.deleteAccount(VALID_EMAIL));
  }
}