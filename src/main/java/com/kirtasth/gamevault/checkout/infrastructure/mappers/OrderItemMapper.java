package com.kirtasth.gamevault.checkout.infrastructure.mappers;

import com.kirtasth.gamevault.checkout.domain.models.OrderItem;
import com.kirtasth.gamevault.checkout.infrastructure.dtos.entities.OrderItemEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {
    @Mapping(target = "orderId", source = "order.id")
    @Mapping(target = "gameId", source = "game.id")
    @Mapping(target = "gameKeyId", source = "gameKey.id")
    OrderItem toDomain(OrderItemEntity entity);

    @Mapping(target = "order.id", source = "orderId")
    @Mapping(target = "game.id", source = "gameId")
    @Mapping(target = "gameKey.id", source = "gameKeyId")
    OrderItemEntity toEntity(OrderItem domain);
}
