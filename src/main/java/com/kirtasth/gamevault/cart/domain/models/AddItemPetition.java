package com.kirtasth.gamevault.cart.domain.models;

public record AddItemPetition(
        Long cartId,
        Long gameId,
        Integer quantity
) {
}
