package com.shoxys.budgetbuddy_backend.Exceptions;

/**
 * Exception thrown when a transaction is not found for a given ID.
 */
public class TransactionNotFoundException extends RuntimeException {

  /**
   * Constructs an exception with a message indicating the transaction ID that was not found.
   *
   * @param transactionId the ID of the transaction that was not found
   */
  public TransactionNotFoundException(Long transactionId) {
    super("Transaction with ID " + transactionId + " not found.");
  }
}