package com.kirtasth.gamevault.checkout.infrastructure.mappers;

import com.kirtasth.gamevault.checkout.domain.models.Order;
import com.kirtasth.gamevault.checkout.infrastructure.dtos.entities.OrderEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {OrderItemMapper.class})
public interface OrderMapper {
    @Mapping(target = "items", source = "items")
    Order toDomain(OrderEntity entity);
    
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "items", source = "items")
    OrderEntity toEntity(Order domain);
}
