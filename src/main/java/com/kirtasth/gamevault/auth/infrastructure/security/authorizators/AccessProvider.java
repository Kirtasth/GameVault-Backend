package com.kirtasth.gamevault.auth.infrastructure.security.authorizators;

import com.kirtasth.gamevault.auth.domain.models.AuthUser;
import com.kirtasth.gamevault.common.models.enums.RoleEnum;
import com.kirtasth.gamevault.users.domain.models.Role;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("permission")
public class AccessProvider {

    public boolean hasRole(RoleEnum role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        var authUser = (AuthUser) authentication.getPrincipal();
        var userRoles = authUser.getRoles();
        if (userRoles == null || userRoles.isEmpty()){
            return false;
        }

        return userRoles.stream()
                .map(Role::getRole)
                .anyMatch(r -> r.equals(role));

    }

    public boolean hasAnyRole(List<RoleEnum> roles) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        var authUser = (AuthUser) authentication.getPrincipal();
        var userRoles = authUser.getRoles();

        if (userRoles == null || userRoles.isEmpty()){
            return false;
        }

        return userRoles.stream()
                .map(Role::getRole)
                .anyMatch(roles::contains);
    }
}
