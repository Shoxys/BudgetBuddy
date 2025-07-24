package com.shoxys.budgetbuddy_backend.Exceptions;

import com.shoxys.budgetbuddy_backend.Config.Constants;

/** Exception thrown when a saving goal is not found. */
public class SavingGoalNotFoundException extends RuntimeException {
  public SavingGoalNotFoundException() {
    super(Constants.SAVING_GOAL_NOT_FOUND);
  }

  public SavingGoalNotFoundException(String message) {
    super(message);
  }
}
