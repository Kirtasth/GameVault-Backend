package com.kirtasth.gamevault.auth.domain.ports.out;

import com.kirtasth.gamevault.auth.domain.models.NewUser;
import com.kirtasth.gamevault.common.models.util.Result;

public interface UsersProviderPort {

    Result<Void> createUser(NewUser newUser);
}
