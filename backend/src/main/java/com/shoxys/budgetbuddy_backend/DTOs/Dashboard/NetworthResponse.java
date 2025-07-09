package com.shoxys.budgetbuddy_backend.DTOs.Dashboard;

import java.math.BigDecimal;
import java.util.List;

public class NetworthResponse {
    private BigDecimal total;
    private List<BreakdownItem> breakdownItems;

    public NetworthResponse(BigDecimal total, List<BreakdownItem> breakdownItems) {
        this.total = total;
        this.breakdownItems = breakdownItems;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }
    public List<BreakdownItem> getBreakdownItems() {
        return breakdownItems;
    }

    public void setBreakdownItems(List<BreakdownItem> breakdownItems) {
        this.breakdownItems = breakdownItems;
    }
}
