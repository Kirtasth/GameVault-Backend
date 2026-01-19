package com.kirtasth.gamevault.users.domain.ports.out;

import com.kirtasth.gamevault.common.models.util.Result;
import org.springframework.security.core.Authentication;

public interface AuthProviderPort {

    Result<Authentication> authenticate(Authentication authentication);
}
