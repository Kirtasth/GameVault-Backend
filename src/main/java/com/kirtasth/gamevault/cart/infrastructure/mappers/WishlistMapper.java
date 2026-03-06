package com.kirtasth.gamevault.cart.infrastructure.mappers;

import com.kirtasth.gamevault.cart.domain.models.Wishlist;
import com.kirtasth.gamevault.cart.infrastructure.dtos.entities.WishlistEntity;
import com.kirtasth.gamevault.cart.infrastructure.dtos.responses.WishlistResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface WishlistMapper {

    Wishlist toDomain(WishlistEntity entity);

    WishlistEntity toEntity(Wishlist domain);

    WishlistResponse toWishlistResponse(Wishlist domain);
}
