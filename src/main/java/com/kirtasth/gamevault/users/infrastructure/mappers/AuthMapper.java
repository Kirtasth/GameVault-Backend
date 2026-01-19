package com.kirtasth.gamevault.users.infrastructure.mappers;

import com.kirtasth.gamevault.users.domain.models.*;
import com.kirtasth.gamevault.users.infrastructure.dtos.entities.RefreshTokenEntity;
import com.kirtasth.gamevault.users.infrastructure.dtos.entities.UserEntity;
import com.kirtasth.gamevault.users.infrastructure.dtos.requests.CredentialsRequest;
import com.kirtasth.gamevault.users.infrastructure.dtos.requests.NewUserRequest;
import com.kirtasth.gamevault.users.infrastructure.dtos.requests.RefreshTokenPetitionRequest;
import com.kirtasth.gamevault.users.infrastructure.dtos.responses.AccessJwtResponse;
import com.kirtasth.gamevault.users.infrastructure.dtos.responses.RefreshTokenResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
uses = {UserEntityResolver.class})
public interface AuthMapper {

    Credentials toCredentials(CredentialsRequest credentialsRequest);

    NewUser toNewUser(NewUserRequest newUserRequest);

    AccessJwtResponse toAccessJwtResponse(AccessJwt accessJwt);

    @Mapping(target = "userId", source = "refreshTokenEntity.user.id")
    RefreshToken toRefreshToken(RefreshTokenEntity refreshTokenEntity);

    @Mapping(target = "user", source = "userId", qualifiedByName = "userIdToEntity")
    @Mapping(target = "id", source = "refreshToken.id")
    @Mapping(target = "createdAt", source = "refreshToken.createdAt")
    RefreshTokenEntity toRefreshTokenEntity(RefreshToken refreshToken);

    RefreshTokenPetition toRefreshTokenPetition(RefreshTokenPetitionRequest refreshTokenPetitionRequest);

    RefreshTokenResponse toRefreshTokenResponse(RefreshToken refreshToken);
}
