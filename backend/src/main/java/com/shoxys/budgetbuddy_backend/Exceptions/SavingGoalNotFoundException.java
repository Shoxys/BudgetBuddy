package com.shoxys.budgetbuddy_backend.Exceptions;

public class SavingGoalNotFoundException extends RuntimeException {
  public SavingGoalNotFoundException(Long id) {
    super("Saving Goal with id " + id + " not found");
  }

  public SavingGoalNotFoundException(String message) {
    super(message);
  }
}
