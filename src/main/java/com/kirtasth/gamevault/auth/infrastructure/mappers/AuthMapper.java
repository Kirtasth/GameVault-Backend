package com.kirtasth.gamevault.auth.infrastructure.mappers;

import com.kirtasth.gamevault.auth.domain.models.AccessJwt;
import com.kirtasth.gamevault.auth.domain.models.Credentials;
import com.kirtasth.gamevault.auth.domain.models.NewUser;
import com.kirtasth.gamevault.auth.infrastructure.dtos.requests.CredentialsRequest;
import com.kirtasth.gamevault.auth.infrastructure.dtos.requests.NewUserRequest;
import com.kirtasth.gamevault.auth.infrastructure.dtos.responses.AccessJwtResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AuthMapper {

    Credentials toCredentials(CredentialsRequest credentialsRequest);

    NewUser toNewUser(NewUserRequest newUserRequest);

    com.kirtasth.gamevault.users.domain.models.NewUser toNewUserInUserModule(NewUser newUser);

    AccessJwtResponse toAccessJwtResponse(AccessJwt accessJwt);
}
