package com.shoxys.budgetbuddy_backend.Security;

import com.shoxys.budgetbuddy_backend.Entities.User;
import com.shoxys.budgetbuddy_backend.Exceptions.UserNotFoundException;
import com.shoxys.budgetbuddy_backend.Repo.UserRepo;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

/**
 * Service for loading user-specific data for Spring Security authentication.
 */
@Service
public class AppUserDetailsService implements UserDetailsService {

  private final UserRepo userRepo;

  /**
   * Constructs an {@code AppUserDetailsService} with the specified user repository.
   *
   * @param userRepo the user repository
   */
  public AppUserDetailsService(UserRepo userRepo) {
    this.userRepo = userRepo;
  }

  /**
   * Loads a user by their email for authentication.
   *
   * @param email the user's email
   * @return the {@link UserDetails} for the user
   * @throws UserNotFoundException if no user is found with the given email
   */
  @Override
  public UserDetails loadUserByUsername(String email) throws UserNotFoundException {
    User user = userRepo.findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
    return new AppUserDetails(user);
  }
}