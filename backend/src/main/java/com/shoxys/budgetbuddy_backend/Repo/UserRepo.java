package com.shoxys.budgetbuddy_backend.Repo;

import com.shoxys.budgetbuddy_backend.Entities.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {
  long getUser_IdByEmail(String email);

  Optional<User> findByEmail(String email);

  boolean existsByEmail(String email);

  Optional<User> getUserByEmail(String email);
}
