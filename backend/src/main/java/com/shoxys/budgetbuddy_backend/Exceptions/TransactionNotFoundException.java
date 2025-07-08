package com.shoxys.budgetbuddy_backend.Exceptions;

public class TransactionNotFoundException extends RuntimeException {
    public TransactionNotFoundException(Long transactionId) {
        super("Transaction with ID " + transactionId + " not found.");
    }
}

