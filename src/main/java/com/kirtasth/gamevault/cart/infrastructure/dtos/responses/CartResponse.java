package com.kirtasth.gamevault.cart.infrastructure.dtos.responses;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@Builder
public class CartResponse {
    private Long id;
    private Long userId;
    private List<CartItemResponse> items;
    private String status;
    private Double totalPrice;
    private Instant createdAt;
    private Instant updatedAt;
}
