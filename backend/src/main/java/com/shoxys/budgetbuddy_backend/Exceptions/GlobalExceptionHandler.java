package com.shoxys.budgetbuddy_backend.Exceptions;

import jakarta.persistence.EntityNotFoundException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<String> handleEntityNotFoundException(EntityNotFoundException exception) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<String> handleBadRequest(IllegalArgumentException exception) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<String> handleAllOthers(Exception exception) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred.");
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, String>> handleValidationErrors(
      MethodArgumentNotValidException ex) {
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult()
        .getFieldErrors()
        .forEach(
            error -> {
              errors.put(error.getField(), error.getDefaultMessage());
            });

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
  }
}
