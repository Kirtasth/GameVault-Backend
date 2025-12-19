package com.kirtasth.gamevault.auth.infrastructure.general_adapters;

import com.kirtasth.gamevault.auth.domain.models.NewUser;
import com.kirtasth.gamevault.auth.domain.ports.out.UsersProviderPort;
import com.kirtasth.gamevault.auth.infrastructure.mappers.AuthMapper;
import com.kirtasth.gamevault.common.models.util.Result;
import com.kirtasth.gamevault.users.domain.models.User;
import com.kirtasth.gamevault.users.domain.ports.in.UserServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UsersProviderAdapter implements UsersProviderPort {

    private final UserServicePort userService;
    private final AuthMapper authMapper;

    @Override
    public Result<Void> createUser(NewUser newUser) {
        var userResult = userService.saveUser(
                authMapper.toNewUserInUserModule(newUser)
        );

        if (userResult instanceof Result.Success<User>) {
            return new Result.Success<>(null);
        }

        var failure = (Result.Failure<User>) userResult;

        return new Result.Failure<>(
                failure.errorCode(),
                failure.errorMsg(),
                failure.errorDetails(),
                failure.exception()
        );
    }
}
