package com.shoxys.budgetbuddy_backend.Controllers;

import com.shoxys.budgetbuddy_backend.DTOs.DashboardResponse;
import com.shoxys.budgetbuddy_backend.Security.AppUserDetails;
import com.shoxys.budgetbuddy_backend.Services.DashboardService;
import com.shoxys.budgetbuddy_backend.Services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
    private final DashboardService dashboardService;
    private final UserService userService;

    public DashboardController(DashboardService dashboardService, UserService userService) {
        this.dashboardService = dashboardService;
        this.userService = userService;
    }

    @GetMapping("/data")
    @ResponseBody
    public DashboardResponse getDashboardData(@AuthenticationPrincipal AppUserDetails userDetails) {
        long userId = userService.getUserIdByEmail(userDetails.getUsername());
        return dashboardService.getDashboardResponse(userId);
    }
}
