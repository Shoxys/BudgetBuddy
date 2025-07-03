package com.shoxys.budgetbuddy_backend.Controllers;

import com.shoxys.budgetbuddy_backend.DTOs.UpdateSavingsRequest;
import com.shoxys.budgetbuddy_backend.Entities.Account;
import com.shoxys.budgetbuddy_backend.Repo.AccountRepo;
import com.shoxys.budgetbuddy_backend.Security.AppUserDetails;
import com.shoxys.budgetbuddy_backend.Services.AccountService;
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
    public ResponseEntity<?> updateSavings(@AuthenticationPrincipal AppUserDetails userDetails, @RequestBody UpdateSavingsRequest updateSavings) {
        accountService.upsertAccount(userDetails.getUsername(), updateSavings.getName(), updateSavings.getAccountType(), updateSavings.getBalance());
        return ResponseEntity.ok("Savings updated successfully");
    }

    @PostMapping("/update-investments")
    public ResponseEntity<?> updateInvestments(@AuthenticationPrincipal AppUserDetails userDetails, @RequestBody UpdateSavingsRequest updateSavings) {
        accountService.upsertAccount(userDetails.getUsername(), updateSavings.getName(), updateSavings.getAccountType(), updateSavings.getBalance());
        return ResponseEntity.ok("Investments updated successfully");
    }
}
