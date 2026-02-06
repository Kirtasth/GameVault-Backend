package com.kirtasth.gamevault.users.domain.ports.in;


import com.kirtasth.gamevault.common.models.util.Result;
import com.kirtasth.gamevault.users.domain.models.AccessJwt;
import com.kirtasth.gamevault.users.domain.models.Credentials;
import com.kirtasth.gamevault.users.domain.models.NewUser;
import com.kirtasth.gamevault.users.domain.models.RefreshTokenPetition;

public interface AuthServicePort {

    Result<AccessJwt> login(Credentials credentials);
    Result<Void> registerUser(NewUser newUser);

    Result<AccessJwt> refresh(RefreshTokenPetition refreshTokenPetition);

    Result<Void> logout(Long userId);
}
