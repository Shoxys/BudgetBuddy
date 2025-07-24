package com.shoxys.budgetbuddy_backend.Repo;

import com.shoxys.budgetbuddy_backend.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for managing {@link User} entities, providing CRUD operations and custom queries
 * for retrieving users by email.
 */
@Repository
public interface UserRepo extends JpaRepository<User, Long> {

  /**
   * Retrieves the user ID by email.
   *
   * @param email the user's email
   * @return the user ID, or null if not found
   */
  Long getUserIdByEmail(@Param("email") String email);

  /**
   * Finds a user by their email.
   *
   * @param email the user's email
   * @return an {@code Optional} containing the user, or empty if not found
   */
  Optional<User> findByEmail(@Param("email") String email);

  /**
   * Checks if a user exists by their email.
   *
   * @param email the user's email
   * @return true if the user exists, false otherwise
   */
  boolean existsByEmail(@Param("email") String email);

  /**
   * Retrieves a user by their email.
   *
   * @param email the user's email
   * @return an {@code Optional} containing the user, or empty if not found
   */
  Optional<User> getUserByEmail(@Param("email") String email);
}