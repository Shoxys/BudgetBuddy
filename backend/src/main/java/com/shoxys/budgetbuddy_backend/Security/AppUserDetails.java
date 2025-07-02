package com.shoxys.budgetbuddy_backend.Security;

import com.shoxys.budgetbuddy_backend.Entities.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class AppUserDetails implements UserDetails {
    private final User user;

    public AppUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public String getUsername(){
        return user.getEmail();
    }

    @Override
    public String getPassword() {
        return user.getHashedPassword();
    }

    @Override
    public boolean isAccountNonExpired() {return true; }

    @Override
    public boolean isAccountNonLocked() {return true; }

    @Override
    public boolean isCredentialsNonExpired() {return true;}

    @Override
    public boolean isEnabled() {return true; }

    public Long getId() {
        return user.getId();
    }

    public User getUser() {
        return user;
    }
}
