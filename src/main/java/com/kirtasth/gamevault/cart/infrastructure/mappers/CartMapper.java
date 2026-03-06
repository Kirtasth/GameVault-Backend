package com.kirtasth.gamevault.cart.infrastructure.mappers;

import com.kirtasth.gamevault.cart.domain.models.AddItemPetition;
import com.kirtasth.gamevault.cart.domain.models.CartItem;
import com.kirtasth.gamevault.cart.domain.models.ShoppingCart;
import com.kirtasth.gamevault.cart.infrastructure.dtos.entities.CartItemEntity;
import com.kirtasth.gamevault.cart.infrastructure.dtos.entities.ShoppingCartEntity;
import com.kirtasth.gamevault.cart.infrastructure.dtos.requests.AddItemPetitionRequest;
import com.kirtasth.gamevault.cart.infrastructure.dtos.responses.CartItemResponse;
import com.kirtasth.gamevault.cart.infrastructure.dtos.responses.CartResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {ShoppingCartEntityResolver.class})
public interface CartMapper {

    ShoppingCart toDomain(ShoppingCartEntity entity);

    ShoppingCartEntity toEntity(ShoppingCart domain);

    @Mapping(target = "cartId", source = "shoppingCart.id")
    CartItem toDomain(CartItemEntity entity);

    @Mapping(target = "shoppingCart", source = "cartId", qualifiedByName = "shoppingCartEntityResolver")
    CartItemEntity toEntity(CartItem domain);

    CartResponse toCartResponse(ShoppingCart cart);

    CartItemResponse toCartItemResponse(CartItem item);

    AddItemPetition toAddItemPetition(AddItemPetitionRequest request);
}
