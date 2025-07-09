package com.shoxys.budgetbuddy_backend.Services;

import com.shoxys.budgetbuddy_backend.DTOs.AuthResponse;
import com.shoxys.budgetbuddy_backend.DTOs.LoginRequest;
import com.shoxys.budgetbuddy_backend.DTOs.RegisterRequest;
import com.shoxys.budgetbuddy_backend.Entities.User;
import com.shoxys.budgetbuddy_backend.Exceptions.EmailExistsException;
import com.shoxys.budgetbuddy_backend.Exceptions.UserNotFoundException;
import com.shoxys.budgetbuddy_backend.Repo.UserRepo;
import com.shoxys.budgetbuddy_backend.Security.AppUserDetails;
import com.shoxys.budgetbuddy_backend.Security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.password4j.Password;
import com.password4j.Hash;

@Service
public class AuthService {
    @Autowired
    UserRepo userRepo;
    AuthenticationManager authenticationManager;
    JwtUtil jwtUtil;

    public AuthResponse authenticate(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
                );
        User user = userRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        AppUserDetails userDetails = new AppUserDetails(user);

        String jwt = jwtUtil.generateToken(userDetails);
        return new AuthResponse(jwt, "User authenticated successfully");
    }


    public void register(RegisterRequest request) {
        if (userRepo.existsByEmail(request.getEmail())) {
            throw new EmailExistsException();
        }
        User user = new User();
        user.setEmail(request.getEmail());
        user.setHashedPassword(hashPassword(request.getPassword()));
        userRepo.save(user);
    }

    public void login(LoginRequest request) {
        User user = userRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid account details"));

        if (!Password.check(request.getPassword(), user.getHashedPassword()).withScrypt()) {
            throw new IllegalArgumentException("Invalid account details");
        }
    }

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
