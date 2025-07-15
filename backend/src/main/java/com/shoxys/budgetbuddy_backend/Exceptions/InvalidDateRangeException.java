package com.shoxys.budgetbuddy_backend.Exceptions;

import java.time.LocalDate;

public class InvalidDateRangeException extends RuntimeException {
  public InvalidDateRangeException(LocalDate start, LocalDate end) {
    super("Start date " + start + " must be before end date " + end);
  }
}
