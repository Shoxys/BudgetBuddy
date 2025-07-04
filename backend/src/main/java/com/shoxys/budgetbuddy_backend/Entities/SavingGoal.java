package com.shoxys.budgetbuddy_backend.Entities;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "savingGoals")
public class SavingGoal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal target;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal contributed;

    @Column(nullable = false)
    private LocalDate date;

    private String imageRef;

    @ManyToOne
    @JoinColumn(name= "account_id", referencedColumnName = "id")
    private Account account;

    @ManyToOne
    @JoinColumn(name= "user_id", referencedColumnName = "id")
    private User user;

    public SavingGoal() {
    }

    public SavingGoal(String title, BigDecimal target, BigDecimal contributed, LocalDate date, String imageRef, Account account, User user) {
        this.title = title;
        this.target = target;
        this.contributed = contributed;
        this.date = date;
        this.imageRef = imageRef;
        this.account = account;
        this.user = user;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BigDecimal getTarget() {
        return target;
    }

    public void setTarget(BigDecimal target) {
        this.target = target;
    }

    public BigDecimal getContributed() {
        return contributed;
    }

    public void setContributed(BigDecimal contributed) {
        this.contributed = contributed;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getImageRef() {
        return imageRef;
    }

    public void setImageRef(String imageRef) {
        this.imageRef = imageRef;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
