package com.shoxys.budgetbuddy_backend.Exceptions;

import java.time.LocalDate;

/**
 * Exception thrown when a date range is invalid (e.g., start date after end date).
 */
public class InvalidDateRangeException extends RuntimeException {
  public InvalidDateRangeException(LocalDate start, LocalDate end) {
    super("Start date " + start + " must be before end date " + end);
  }
}