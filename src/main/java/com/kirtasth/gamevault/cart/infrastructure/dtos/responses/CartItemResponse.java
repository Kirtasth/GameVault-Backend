package com.kirtasth.gamevault.cart.infrastructure.dtos.responses;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class CartItemResponse {
    private Long id;
    private Long gameId;
    private Integer quantity;
    private Double priceAtAddition;
    private Instant createdAt;
    private Instant updatedAt;
}
