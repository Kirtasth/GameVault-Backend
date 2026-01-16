package com.kirtasth.gamevault.users.domain.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
public class AuthUser implements UserDetails {

    private Long id;

    private String username;

    private String email;

    private String password;

    private String avatarUrl;

    private String bio;

    private boolean emailVerified;

    private boolean accountEnabled;

    private boolean accountExpired;

    private boolean accountLocked;

    private boolean credentialsExpired;

    private String lockReason;

    private Instant lockInstant;

    private Instant createdAt;

    private Instant updatedAt;

    private Instant deletedAt;

    private List<UserIdentity> userIdentities;

    private List<Role> roles;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public boolean isAccountNonExpired() {
        return !this.accountExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !this.accountLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return !this.accountExpired;
    }

    @Override
    public boolean isEnabled() {
        return this.accountEnabled;
    }
}
