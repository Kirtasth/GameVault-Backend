package com.kirtasth.gamevault.users.infrastructure.security;

import com.kirtasth.gamevault.common.domain.models.enums.RoleEnum;
import com.kirtasth.gamevault.users.domain.models.AuthUser;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component("permission")
public class PermissionChecker {

    public AuthorizationManager<RequestAuthorizationContext> hasRole(RoleEnum roleEnum) {
        return (auth, requestContext) -> {
            var authentication = auth.get();

            if (authentication == null || !authentication.isAuthenticated()
                    || !(authentication.getPrincipal() instanceof AuthUser authUser)) {
                return new AuthorizationDecision(false);
            }

            boolean granted = authUser.getRoles().stream()
                    .map(role -> role.getRole().name())
                    .anyMatch(roleEnum.name()::equals);
            return new AuthorizationDecision(granted);
        };
    }

    public AuthorizationManager<RequestAuthorizationContext> isAuthenticated() {
        return (auth, requestContext) -> {
            var authentication = auth.get();

            if (authentication == null || !authentication.isAuthenticated()
                    || !(authentication.getPrincipal() instanceof AuthUser)) {
                return new AuthorizationDecision(false);
            }

            return new AuthorizationDecision(true);
        };
    }

    public AuthorizationManager<RequestAuthorizationContext> isAdmin() {
        return (auth, requestContext) -> {
            var authentication = auth.get();

            if (authentication == null || !authentication.isAuthenticated()
                    || !(authentication.getPrincipal() instanceof AuthUser authUser)) {
                return new AuthorizationDecision(false);
            }
            boolean granted = authUser.getRoles().stream()
                    .map(role -> role.getRole().name())
                    .anyMatch(RoleEnum.ADMIN.name()::equals);
            return new AuthorizationDecision(granted);
        };
    }

    public AuthorizationManager<RequestAuthorizationContext> isOwner(String pathVariable) {
        return (auth, requestContext) -> {
            var authentication = auth.get();

            if (authentication == null || !authentication.isAuthenticated()
                    || !(authentication.getPrincipal() instanceof AuthUser authUser)) {
                return new AuthorizationDecision(false);
            }

            var userId = authUser.getId();
            if (!userId.equals(Long.valueOf(requestContext.getVariables().get(pathVariable)))) {
                return new AuthorizationDecision(false);
            }

            return new AuthorizationDecision(true);
        };
    }

    public AuthorizationManager<RequestAuthorizationContext> isOwnerOrAdmin(String pathVariable) {
        return (auth, requestContext) -> {
            boolean isOwner = Objects.requireNonNull(isOwner(pathVariable).authorize(auth, requestContext)).isGranted();
            boolean isAdmin = Objects.requireNonNull(isAdmin().authorize(auth, requestContext)).isGranted();

            return new AuthorizationDecision(isOwner || isAdmin);
        };
    }
}
