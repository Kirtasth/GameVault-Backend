package com.kirtasth.gamevault.auth.infrastructure.mappers;

import com.kirtasth.gamevault.auth.domain.models.AccessJwt;
import com.kirtasth.gamevault.auth.domain.models.Credentials;
import com.kirtasth.gamevault.auth.domain.models.NewUser;
import com.kirtasth.gamevault.auth.domain.models.RefreshToken;
import com.kirtasth.gamevault.auth.infrastructure.dtos.entities.RefreshTokenEntity;
import com.kirtasth.gamevault.auth.infrastructure.dtos.requests.CredentialsRequest;
import com.kirtasth.gamevault.auth.infrastructure.dtos.requests.NewUserRequest;
import com.kirtasth.gamevault.auth.infrastructure.dtos.responses.AccessJwtResponse;
import com.kirtasth.gamevault.users.domain.models.User;
import com.kirtasth.gamevault.users.infrastructure.dtos.entities.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AuthMapper {

    Credentials toCredentials(CredentialsRequest credentialsRequest);

    NewUser toNewUser(NewUserRequest newUserRequest);

    com.kirtasth.gamevault.users.domain.models.NewUser toNewUserInUserModule(NewUser newUser);

    AccessJwtResponse toAccessJwtResponse(AccessJwt accessJwt);

    @Mapping(target = "userId", expression = "java(refreshToken.getUserId())")
    RefreshToken toRefreshToken(RefreshTokenEntity refreshTokenEntity);

    @Mapping(target = "userId", source = "user")
    @Mapping(target = "id", source = "refreshToken.id")
    @Mapping(target = "createdAt", source = "refreshToken.createdAt")
    RefreshTokenEntity toRefreshTokenEntity(RefreshToken refreshToken, User user);

    UserEntity toUserEntity(User user);
}
