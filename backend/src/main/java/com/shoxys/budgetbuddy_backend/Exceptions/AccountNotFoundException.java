package com.shoxys.budgetbuddy_backend.Exceptions;

import com.shoxys.budgetbuddy_backend.Entities.User;

public class AccountNotFoundException extends RuntimeException {
        public AccountNotFoundException(User user) {
            super("Account not found for specified user");
        }
}
