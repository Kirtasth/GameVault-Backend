package com.kirtasth.gamevault.auth.infrastructure.security;

import com.kirtasth.gamevault.auth.domain.models.AuthUser;
import com.kirtasth.gamevault.common.models.enums.RoleEnum;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component("permission")
public class PermissionChecker {

    public AuthorizationManager<RequestAuthorizationContext> hasRole(RoleEnum roleEnum) {
        return (auth, requestContext) -> {
            boolean granted = auth.get().getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .anyMatch(roleEnum.name()::equals);
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
            String idFromPath = requestContext.getVariables().get(pathVariable);
            boolean isOwner = authUser.getId().toString().equals(idFromPath);

            return new AuthorizationDecision(isOwner);
        };
    }

    public AuthorizationManager<RequestAuthorizationContext> isAdmin() {
        return (auth, requestContext) -> {
            boolean granted = auth.get().getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .anyMatch(RoleEnum.ADMIN.name()::equals);
            return new AuthorizationDecision(granted);
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
