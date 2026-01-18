package com.kirtasth.gamevault.users.infrastructure.security;

import com.kirtasth.gamevault.common.models.util.Result;
import com.kirtasth.gamevault.users.domain.ports.out.AuthProviderPort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthProviderAdapter implements AuthProviderPort {

    private final AuthenticationProvider authenticationProvider;

    @Override
    public Result<Authentication> authenticate(Authentication authentication) {

        var res = this.authenticationProvider.authenticate(authentication);
        return new Result.Success<>(res);
    }
}
