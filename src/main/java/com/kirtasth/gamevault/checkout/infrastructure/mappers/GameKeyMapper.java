package com.kirtasth.gamevault.checkout.infrastructure.mappers;

import com.kirtasth.gamevault.checkout.domain.models.GameKey;
import com.kirtasth.gamevault.checkout.infrastructure.dtos.entities.GameKeyEntity;
import com.kirtasth.gamevault.checkout.infrastructure.dtos.responses.GameKeyResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GameKeyMapper {

    @Mapping(target = "gameId", source = "game.id")
    @Mapping(target = "isUsed", expression = "java(entity.getUsedAt() != null)")
    GameKey toDomain(GameKeyEntity entity);

    @Mapping(target = "game.id", source = "gameId")
    GameKeyEntity toEntity(GameKey domain);

    GameKeyResponse toResponse(GameKey domain);
}
