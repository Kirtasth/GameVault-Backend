package com.kirtasth.gamevault.users.application.services;

import com.kirtasth.gamevault.users.application.exception.UserNotFoundException;
import com.kirtasth.gamevault.users.domain.models.AuthUser;
import com.kirtasth.gamevault.users.domain.ports.in.UserServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceAdapter implements UserDetailsService {

    private final UserServicePort userServicePort;

    @Override
    public UserDetails loadUserByUsername(String username) {

        try {
            var serviceUser = userServicePort.getUserByEmail(username);
            var user = new AuthUser();
            user.setId(serviceUser.getId());
            user.setUsername(serviceUser.getUsername());
            user.setEmail(serviceUser.getEmail());
            user.setPassword(serviceUser.getPassword());
            user.setAvatarUrl(serviceUser.getAvatarUrl());
            user.setBio(serviceUser.getBio());
            user.setEmailVerified(serviceUser.isEmailVerified());
            user.setAccountEnabled(serviceUser.isAccountEnabled());
            user.setAccountExpired(serviceUser.isAccountExpired());
            user.setAccountLocked(serviceUser.isAccountLocked());
            user.setCredentialsExpired(serviceUser.isCredentialsExpired());
            user.setLockReason(serviceUser.getLockReason());
            user.setLockInstant(serviceUser.getLockInstant());
            user.setCreatedAt(serviceUser.getCreatedAt());
            user.setUpdatedAt(serviceUser.getUpdatedAt());
            user.setDeletedAt(serviceUser.getDeletedAt());

            user.setRoles(userServicePort.getRolesByUserId(serviceUser.getId()));

            return user;
        } catch (UserNotFoundException e) {
            throw new UsernameNotFoundException("User not found with username: " + username + ".");
        }
    }
}
