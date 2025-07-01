package com.shoxys.budgetbuddy_backend.Entities;

import com.shoxys.budgetbuddy_backend.Enum.SourceType;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private double amount;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String category;

    private String merchant;

    @Column(nullable = false)
    private float balanceAtTransaction;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SourceType source;

    @ManyToOne
    @JoinColumn(name= "account_id", referencedColumnName = "id")
    private Account account;

    @ManyToOne
    @JoinColumn(name= "user_id", referencedColumnName = "id")
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getMerchant() {
        return merchant;
    }

    public void setMerchant(String merchant) {
        this.merchant = merchant;
    }

    public float getBalanceAtTransaction() {
        return balanceAtTransaction;
    }

    //TODO: Add logic for this

    public void setBalanceAtTransaction(float balanceAtTransaction) {
        this.balanceAtTransaction = balanceAtTransaction;
    }

    public SourceType getSource() {
        return source;
    }

    public void setSource(SourceType source) {
        this.source = source;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }
}
