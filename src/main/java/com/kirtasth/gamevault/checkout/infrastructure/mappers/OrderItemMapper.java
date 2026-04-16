package com.kirtasth.gamevault.checkout.infrastructure.mappers;

import com.kirtasth.gamevault.catalog.infrastructure.mappers.GameEntityResolver;
import com.kirtasth.gamevault.checkout.domain.models.OrderItem;
import com.kirtasth.gamevault.checkout.infrastructure.dtos.entities.OrderItemEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {GameKeyEntityResolver.class, GameEntityResolver.class})
public interface OrderItemMapper {
    @Mapping(target = "orderId", source = "order.id")
    @Mapping(target = "gameId", source = "game.id")
    @Mapping(target = "gameKeyId", source = "gameKey.id")
    OrderItem toDomain(OrderItemEntity entity);

    @Mapping(target = "order.id", source = "orderId")
    @Mapping(target = "game", source = "gameId", qualifiedByName = "gameIdToEntity")
    @Mapping(target = "gameKey", source = "gameKeyId", qualifiedByName = "gameKeyIdToEntity")
    OrderItemEntity toEntity(OrderItem domain);
}
