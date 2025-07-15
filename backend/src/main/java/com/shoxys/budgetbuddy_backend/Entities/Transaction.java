package com.shoxys.budgetbuddy_backend.Entities;

import com.shoxys.budgetbuddy_backend.Enums.SourceType;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String category;

    private String merchant;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal balanceAtTransaction;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SourceType source;

    @ManyToOne
    @JoinColumn(name= "account_id", referencedColumnName = "id")
    private Account account;

    @ManyToOne
    @JoinColumn(name= "user_id", referencedColumnName = "id")
    private User user;

    public Transaction() {

    }

    public Transaction(LocalDate date, BigDecimal amount, String description, String category, String merchant, BigDecimal balanceAtTransaction, SourceType source, Account account, User user) {
        this.date = date;
        this.amount = amount;
        this.description = description;
        this.category = category;
        this.merchant = merchant;
        this.balanceAtTransaction = balanceAtTransaction;
        this.source = source;
        this.account = account;
        this.user = user;
    }

    // Copy constructor for tests
    public Transaction(Transaction other) {
        this.id = other.id;
        this.date = other.date;
        this.amount = other.amount;
        this.description = other.description;
        this.category = other.category;
        this.merchant = other.merchant;
        this.balanceAtTransaction = other.balanceAtTransaction;
        this.source = other.source;
        this.account = other.account;
        this.user = other.user;
    }

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

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
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

    public BigDecimal getBalanceAtTransaction() {
        return balanceAtTransaction;
    }

    //TODO: Add logic for this

    public void setBalanceAtTransaction(BigDecimal balanceAtTransaction) {
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
