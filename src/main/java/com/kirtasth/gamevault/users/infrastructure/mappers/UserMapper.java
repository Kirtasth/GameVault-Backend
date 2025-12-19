package com.kirtasth.gamevault.users.infrastructure.mappers;

import com.kirtasth.gamevault.users.domain.models.NewUser;
import com.kirtasth.gamevault.users.domain.models.User;
import com.kirtasth.gamevault.users.domain.models.UserCriteria;
import com.kirtasth.gamevault.users.infrastructure.dtos.entities.UserEntity;
import com.kirtasth.gamevault.users.infrastructure.dtos.requests.NewUserDto;
import com.kirtasth.gamevault.users.infrastructure.dtos.requests.UserCriteriaDto;
import com.kirtasth.gamevault.users.infrastructure.dtos.responses.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    User toUser(UserEntity userEntity);
    UserEntity toUserEntity(User user);

    NewUser toNewUser(NewUserDto newUserDto);

    UserResponse toUserResponse(User user);

    UserCriteria toUserCriteria(UserCriteriaDto userCriteriaDto);
}
