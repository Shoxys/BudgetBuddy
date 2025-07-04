package com.shoxys.budgetbuddy_backend.Controllers;

import com.shoxys.budgetbuddy_backend.DTOs.UpdateAccountRequest;
import com.shoxys.budgetbuddy_backend.Security.AppUserDetails;
import com.shoxys.budgetbuddy_backend.Services.AccountService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/account")
public class AccountController {
    @Autowired
    private AccountService accountService;

    @PostMapping("/update-savings")
    public ResponseEntity<?> updateSavings(@AuthenticationPrincipal AppUserDetails userDetails, @Valid @RequestBody UpdateAccountRequest updateSavings) {
        accountService.upsertAccount(userDetails.getUsername(), updateSavings.getName(), updateSavings.getAccountType(), updateSavings.getBalance());
        return ResponseEntity.ok("Savings updated successfully");
    }

    @PostMapping("/update-investments")
    public ResponseEntity<?> updateInvestments(@AuthenticationPrincipal AppUserDetails userDetails, @Valid @RequestBody UpdateAccountRequest updateInvestments) {
        accountService.upsertAccount(userDetails.getUsername(), updateInvestments.getName(), updateInvestments.getAccountType(), updateInvestments.getBalance());
        return ResponseEntity.ok("Investments updated successfully");
    }
}
