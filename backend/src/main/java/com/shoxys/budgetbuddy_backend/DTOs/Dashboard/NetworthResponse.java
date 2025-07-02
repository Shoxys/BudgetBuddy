package com.shoxys.budgetbuddy_backend.DTOs.Dashboard;

import java.math.BigDecimal;
import java.util.List;

public class NetworthResponse {
    private BigDecimal total;
    private List<BreakdownItem> breakdown;

    public NetworthResponse(BigDecimal total, List<BreakdownItem> breakdown) {
        this.total = total;
        this.breakdown = breakdown;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public List<BreakdownItem> getBreakdown() {
        return breakdown;
    }

    public void setBreakdown(List<BreakdownItem> breakdown) {
        this.breakdown = breakdown;
    }
}
