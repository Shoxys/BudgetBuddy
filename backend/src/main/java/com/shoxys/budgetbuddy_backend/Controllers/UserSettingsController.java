package com.shoxys.budgetbuddy_backend.Controllers;

import com.shoxys.budgetbuddy_backend.DTOs.AuthResponse;
import com.shoxys.budgetbuddy_backend.DTOs.ChangePasswordRequest;
import com.shoxys.budgetbuddy_backend.DTOs.UpdateEmailRequest;
import com.shoxys.budgetbuddy_backend.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/settings")
public class UserSettingsController {
    @Autowired
    UserService userService;

    public UserSettingsController(UserService userService) {
        this.userService = userService;
    }

    @PutMapping("/change-password")
    public ResponseEntity<String> updatePassword(@AuthenticationPrincipal UserDetails userDetails,
                                                 @RequestBody ChangePasswordRequest request) {
        userService.changePassword(userDetails.getUsername(), request);
        return ResponseEntity.ok("Password changed successfully");
    }

    @PutMapping("/update-email")
    public ResponseEntity<?> updateEmail(@AuthenticationPrincipal UserDetails userDetails,
                                              @RequestBody UpdateEmailRequest request) {
        AuthResponse response = userService.updateEmail(userDetails.getUsername(), request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete-account")
    public ResponseEntity<?> deleteAccount(@AuthenticationPrincipal UserDetails userDetails) {
        userService.deleteAccount(userDetails.getUsername());
        return ResponseEntity.ok("Account deleted successfully");
    }


}
