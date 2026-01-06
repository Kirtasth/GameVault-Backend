package com.kirtasth.gamevault.auth.domain.ports.in;


import com.kirtasth.gamevault.auth.domain.models.AccessJwt;
import com.kirtasth.gamevault.auth.domain.models.Credentials;
import com.kirtasth.gamevault.auth.domain.models.NewUser;
import com.kirtasth.gamevault.common.models.util.Result;

public interface AuthServicePort {

    Result<AccessJwt> login(Credentials credentials);
    Result<Void> registerUser(NewUser newUser);
    Result<AccessJwt> refresh(String refreshToken);
}
