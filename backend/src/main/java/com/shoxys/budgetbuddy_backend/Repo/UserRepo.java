package com.shoxys.budgetbuddy_backend.Repo;


import com.shoxys.budgetbuddy_backend.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<User, Integer> {
}
