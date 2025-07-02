package com.shoxys.budgetbuddy_backend.DTOs;

import com.shoxys.budgetbuddy_backend.Entities.Transaction;

import java.time.LocalDate;
import java.util.List;

public class TransactionSummaryResponse {
    private List<Transaction> transactions;
    private long count;
    private LocalDate earliest;
    private LocalDate latest;

    public TransactionSummaryResponse(List<Transaction> transactions, long count, LocalDate earliest, LocalDate latest) {
        this.transactions = transactions;
        this.count = count;
        this.earliest = earliest;
        this.latest = latest;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public LocalDate getEarliest() {
        return earliest;
    }

    public void setEarliest(LocalDate earliest) {
        this.earliest = earliest;
    }

    public LocalDate getLatest() {
        return latest;
    }

    public void setLatest(LocalDate latest) {
        this.latest = latest;
    }
}