package com.shoxys.budgetbuddy_backend.Services;

import com.shoxys.budgetbuddy_backend.DTOs.AuthResponse;
import com.shoxys.budgetbuddy_backend.DTOs.ChangePasswordRequest;
import com.shoxys.budgetbuddy_backend.DTOs.UpdateEmailRequest;
import com.shoxys.budgetbuddy_backend.Entities.User;
import com.shoxys.budgetbuddy_backend.Exceptions.EmailExistsException;
import com.shoxys.budgetbuddy_backend.Exceptions.UserNotFoundException;
import com.shoxys.budgetbuddy_backend.Repo.UserRepo;
import com.shoxys.budgetbuddy_backend.Security.AppUserDetails;
import com.shoxys.budgetbuddy_backend.Security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService  {
    @Autowired
    private UserRepo userRepo;
    private AuthService authService;
    JwtUtil jwtUtil;

    public long getUserIdByEmail(String email) {
        return userRepo.getUser_IdByEmail(email);
    }

    public void changePassword(String email, ChangePasswordRequest request) {
        User user = userRepo.getUserByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Incorrect account details"));

        if (!authService.verifyPassword(request.getCurrentPassword(), user.getHashedPassword())){
            throw new SecurityException("Incorrect password");
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new SecurityException("Passwords do not match");
        }

        user.setHashedPassword(authService.hashPassword(request.getNewPassword()));
        userRepo.save(user);
    }

    public AuthResponse updateEmail(String email, UpdateEmailRequest request) {
        User user = userRepo.getUserByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Incorrect account details"));

        if (!email.equals(request.getCurrentEmail())){
            throw new SecurityException("Incorrect account details");
        }

        if (!authService.verifyPassword(request.getConfirmPassword(), user.getHashedPassword())){
            throw new SecurityException("Incorrect account details");
        }

        if (request.getCurrentEmail().equals(request.getNewEmail())){
            throw new EmailExistsException("Email is the same as current");
        }

        user.setEmail(request.getNewEmail());
        userRepo.save(user);

        // Generate new authentication token after username update
        AppUserDetails userDetails = new AppUserDetails(user);
        String newToken = jwtUtil.generateToken(userDetails);
        return new AuthResponse(newToken, "Email updated successfully");
    }

    public void deleteAccount(String email) {
        User user = userRepo.getUserByEmail(email)
                        .orElseThrow(() -> new UserNotFoundException("User doesn't exist"));
        userRepo.delete(user);
    }
}
