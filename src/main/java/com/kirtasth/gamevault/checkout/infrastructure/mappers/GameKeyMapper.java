package com.kirtasth.gamevault.checkout.infrastructure.mappers;

import com.kirtasth.gamevault.catalog.infrastructure.mappers.GameEntityResolver;
import com.kirtasth.gamevault.checkout.domain.models.GameKey;
import com.kirtasth.gamevault.checkout.infrastructure.dtos.entities.GameKeyEntity;
import com.kirtasth.gamevault.checkout.infrastructure.dtos.responses.GameKeyResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {GameEntityResolver.class})
public interface GameKeyMapper {

    @Mapping(target = "gameId", source = "game.id")
    @Mapping(target = "isUsed", expression = "java(entity.getUsedAt() != null)")
    GameKey toDomain(GameKeyEntity entity);

    @Mapping(target = "game", source = "gameId", qualifiedByName = "gameIdToEntity")
    GameKeyEntity toEntity(GameKey domain);

    GameKeyResponse toResponse(GameKey domain);
}
