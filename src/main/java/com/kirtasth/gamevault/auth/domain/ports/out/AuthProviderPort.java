package com.kirtasth.gamevault.auth.domain.ports.out;

import com.kirtasth.gamevault.common.models.util.Result;
import org.springframework.security.core.Authentication;

public interface AuthProviderPort {

    Result<Authentication> authenticate(Authentication authentication);
}
