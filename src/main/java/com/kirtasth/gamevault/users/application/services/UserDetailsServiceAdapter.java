package com.kirtasth.gamevault.users.application.services;

import com.kirtasth.gamevault.common.models.util.Result;
import com.kirtasth.gamevault.users.domain.models.AuthUser;
import com.kirtasth.gamevault.users.domain.models.User;
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
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var res = userServicePort.getUserByEmail(username);

        if (res instanceof Result.Success<User>(User serviceUser)) {
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
            user.setUserIdentities(serviceUser.getIdentities());

            user.setRoles(userServicePort.getRolesByUserId(serviceUser.getId()));

            return user;
        }
        throw new UsernameNotFoundException("Could not get user with email: " + username);
    }
}
