package com.shoxys.budgetbuddy_backend.Exceptions;

import com.shoxys.budgetbuddy_backend.Config.Constants;
import com.shoxys.budgetbuddy_backend.DTOs.ErrorResponse;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Centralized exception handler for REST controllers, returning standardized error responses.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  /**
   * Handles EntityNotFoundException, returning a 404 response.
   *
   * @param ex the exception
   * @return ResponseEntity with ErrorResponse
   */
  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException ex) {
    String message = truncateMessage("Entity not found: " + ex.getMessage());
    log.warn("Entity not found: {}", message);
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse(message, HttpStatus.NOT_FOUND));
  }

  /**
   * Handles UserNotFoundException, returning a 404 response.
   *
   * @param ex the exception
   * @return ResponseEntity with ErrorResponse
   */
  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException ex) {
    String message = truncateMessage(ex.getMessage());
    log.warn("User not found: {}", message);
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse(message, HttpStatus.NOT_FOUND));
  }

  /**
   * Handles InvalidPasswordException, returning a 400 response.
   *
   * @param ex the exception
   * @return ResponseEntity with ErrorResponse
   */
  @ExceptionHandler(InvalidPasswordException.class)
  public ResponseEntity<ErrorResponse> handleInvalidPasswordException(InvalidPasswordException ex) {
    String message = truncateMessage(ex.getMessage());
    log.warn("Invalid password: {}", message);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponse(message, HttpStatus.BAD_REQUEST));
  }

  /**
   * Handles InvalidEmailFormatException, returning a 400 response.
   *
   * @param ex the exception
   * @return ResponseEntity with ErrorResponse
   */
  @ExceptionHandler(InvalidEmailFormatException.class)
  public ResponseEntity<ErrorResponse> handleInvalidEmailFormatException(InvalidEmailFormatException ex) {
    String message = truncateMessage(ex.getMessage());
    log.warn("Invalid email format: {}", message);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponse(message, HttpStatus.BAD_REQUEST));
  }

  /**
   * Handles EmailExistsException, returning a 409 response.
   *
   * @param ex the exception
   * @return ResponseEntity with ErrorResponse
   */
  @ExceptionHandler(EmailExistsException.class)
  public ResponseEntity<ErrorResponse> handleEmailExistsException(EmailExistsException ex) {
    String message = truncateMessage(ex.getMessage());
    log.warn("Email already exists: {}", message);
    return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(new ErrorResponse(message, HttpStatus.CONFLICT));
  }

  /**
   * Handles MissingFieldException, returning a 400 response.
   *
   * @param ex the exception
   * @return ResponseEntity with ErrorResponse
   */
  @ExceptionHandler(MissingFieldException.class)
  public ResponseEntity<ErrorResponse> handleMissingFieldException(MissingFieldException ex) {
    String message = truncateMessage(ex.getMessage());
    log.warn("Missing field: {}", message);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponse(message, HttpStatus.BAD_REQUEST));
  }

  /**
   * Handles AccountNotFoundException, returning a 404 response.
   *
   * @param ex the exception
   * @return ResponseEntity with ErrorResponse
   */
  @ExceptionHandler(AccountNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleAccountNotFoundException(AccountNotFoundException ex) {
    String message = truncateMessage(ex.getMessage());
    log.warn("Account not found: {}", message);
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse(message, HttpStatus.NOT_FOUND));
  }

  /**
   * Handles InvalidCredentialsException, returning a 401 response.
   *
   * @param ex the exception
   * @return ResponseEntity with ErrorResponse
   */
  @ExceptionHandler(InvalidCredentialsException.class)
  public ResponseEntity<ErrorResponse> handleInvalidCredentialsException(InvalidCredentialsException ex) {
    String message = truncateMessage(ex.getMessage());
    log.warn("Invalid credentials: {}", message);
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(new ErrorResponse(message, HttpStatus.UNAUTHORIZED));
  }

  /**
   * Handles PasswordMismatchException, returning a 400 response.
   *
   * @param ex the exception
   * @return ResponseEntity with ErrorResponse
   */
  @ExceptionHandler(PasswordMismatchException.class)
  public ResponseEntity<ErrorResponse> handlePasswordMismatchException(PasswordMismatchException ex) {
    String message = truncateMessage(ex.getMessage());
    log.warn("Password mismatch: {}", message);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponse(message, HttpStatus.BAD_REQUEST));
  }

  /**
   * Handles SavingGoalNotFoundException, returning a 404 response.
   *
   * @param ex the exception
   * @return ResponseEntity with ErrorResponse
   */
  @ExceptionHandler(SavingGoalNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleSavingGoalNotFoundException(SavingGoalNotFoundException ex) {
    String message = truncateMessage(ex.getMessage());
    log.warn("Saving goal not found: {}", message);
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse(message, HttpStatus.NOT_FOUND));
  }

  /**
   * Handles InvalidDateRangeException, returning a 400 response.
   *
   * @param ex the exception
   * @return ResponseEntity with ErrorResponse
   */
  @ExceptionHandler(InvalidDateRangeException.class)
  public ResponseEntity<ErrorResponse> handleInvalidDateRangeException(InvalidDateRangeException ex) {
    String message = truncateMessage(ex.getMessage());
    log.warn("Invalid date range: {}", message);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponse(message, HttpStatus.BAD_REQUEST));
  }

  /**
   * Handles TransactionNotFoundException, returning a 404 response.
   *
   * @param ex the exception
   * @return ResponseEntity with ErrorResponse
   */
  @ExceptionHandler(TransactionNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleTransactionNotFoundException(TransactionNotFoundException ex) {
    String message = truncateMessage(ex.getMessage());
    log.warn("Transaction not found: {}", message);
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse(message, HttpStatus.NOT_FOUND));
  }

  /**
   * Handles IllegalArgumentException, returning a 400 response.
   *
   * @param ex the exception
   * @return ResponseEntity with ErrorResponse
   */
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
    String message = truncateMessage(ex.getMessage());
    log.warn("Invalid request: {}", message);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponse(message, HttpStatus.BAD_REQUEST));
  }

  /**
   * Handles validation errors from MethodArgumentNotValidException, returning a 400 response.
   *
   * @param ex the exception
   * @return ResponseEntity with ErrorResponse
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
    log.error("Validation error occurred: ", ex);
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult()
            .getFieldErrors()
            .forEach(error -> {
              String field = error.getField();
              String message = error.getDefaultMessage();
              errors.put(field, message);
              log.info("Validation error for field '{}': {}", field, message);
            });

    String message = truncateMessage(String.join("; ", errors.values()));
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponse(message, HttpStatus.BAD_REQUEST));
  }

  /**
   * Handles uncaught exceptions, returning a 500 response.
   *
   * @param ex the exception
   * @return ResponseEntity with ErrorResponse
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
    String message = truncateMessage("An unexpected error occurred");
    log.error("Unhandled exception occurred: ", ex);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ErrorResponse(message, HttpStatus.INTERNAL_SERVER_ERROR));
  }

  /**
   * Truncates error messages to ensure they do not exceed the maximum length.
   *
   * @param message the error message
   * @return truncated message
   */
  private String truncateMessage(String message) {
    if (message != null && message.length() > Constants.MAX_ERROR_MESSAGE_LENGTH) {
      return message.substring(0, Constants.MAX_ERROR_MESSAGE_LENGTH);
    }
    return message;
  }
}