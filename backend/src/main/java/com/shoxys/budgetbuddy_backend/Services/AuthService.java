package com.shoxys.budgetbuddy_backend.Services;

import org.springframework.stereotype.Service;
import com.password4j.Password;
import com.password4j.Hash;

@Service
public class AuthService {

    // Hash plain text password with a random salt and return hash string
    public String hashPassword(String plainPassword) {
        Hash hashed = Password.hash(plainPassword).addRandomSalt().withScrypt();
        return hashed.getResult();
    }

    // Verify that plain text password is the same as stored hashed password
    public boolean verifyPassword(String plainPassword, String hashedPasswordFromDb) {
        return  Password.check(plainPassword, hashedPasswordFromDb).withScrypt();
    }
}
