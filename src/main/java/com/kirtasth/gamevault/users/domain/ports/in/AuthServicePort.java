package com.kirtasth.gamevault.users.domain.ports.in;


import com.kirtasth.gamevault.common.models.util.Result;
import com.kirtasth.gamevault.users.domain.models.*;

public interface AuthServicePort {

    Result<AccessJwt> login(Credentials credentials);
    Result<Void> registerUser(NewUser newUser);
    Result<RefreshToken> refresh(RefreshTokenPetition refreshTokenPetition);
    Result<Void> logout(Long userId);
}
