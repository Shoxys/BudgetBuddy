package com.shoxys.budgetbuddy_backend.Security;

import com.shoxys.budgetbuddy_backend.Entities.User;
import java.util.Collection;
import java.util.Collections;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Implementation of {@link UserDetails} for Spring Security, wrapping a {@link User} entity to
 * provide authentication details such as username, password, and authorities.
 */
public class AppUserDetails implements UserDetails {

  private final User user;

  /**
   * Constructs an {@code AppUserDetails} instance with the specified user.
   *
   * @param user the user entity
   */
  public AppUserDetails(User user) {
    this.user = user;
  }

  /**
   * Returns the authorities granted to the user. Currently, returns an empty list (no roles).
   *
   * @return an empty collection of authorities
   */
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return Collections.emptyList();
  }

  @Override
  public String getUsername() {
    return user.getEmail();
  }

  @Override
  public String getPassword() {
    return user.getHashedPassword();
  }

  /**
   * Indicates whether the user's account is non-expired.
   *
   * @return true (account is always non-expired)
   */
  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  /**
   * Indicates whether the user's account is non-locked.
   *
   * @return true (account is always non-locked)
   */
  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  /**
   * Indicates whether the user's credentials are non-expired.
   *
   * @return true (credentials are always non-expired)
   */
  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  /**
   * Indicates whether the user is enabled.
   *
   * @return true (user is always enabled)
   */
  @Override
  public boolean isEnabled() {
    return true;
  }

  public Long getId() {
    return user.getId();
  }

  public User getUser() {
    return user;
  }
}
