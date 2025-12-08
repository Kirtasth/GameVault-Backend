package com.kirtasth.gamevault.users.infrastructure.mappers;

import com.kirtasth.gamevault.users.domain.User;
import com.kirtasth.gamevault.users.domain.UserCriteria;
import com.kirtasth.gamevault.users.infrastructure.dtos.entities.UserEntity;
import com.kirtasth.gamevault.users.infrastructure.dtos.requests.UserCriteriaDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    User toUser(UserEntity userEntity);
    UserEntity toUserEntity(User user);

    UserCriteria toUserCriteria(UserCriteriaDto userCriteriaDto);
}
