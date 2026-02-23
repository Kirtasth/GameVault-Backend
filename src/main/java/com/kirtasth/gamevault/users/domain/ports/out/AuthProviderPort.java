package com.kirtasth.gamevault.users.domain.ports.out;

import org.springframework.security.core.Authentication;

public interface AuthProviderPort {

    Authentication authenticate(Authentication authentication);
}
