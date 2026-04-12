package com.kirtasth.gamevault.checkout.domain.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    private Long id;
    private Long userId;
    private Double totalPrice;
    private OrderStatus status;
    private String stripeSessionId;
    private List<OrderItem> items;
    private Instant createdAt;
    private Instant updatedAt;
}
