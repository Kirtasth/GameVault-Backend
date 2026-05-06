package com.kirtasth.gamevault.checkout.domain.ports.out;

import com.kirtasth.gamevault.checkout.domain.models.Order;
import java.util.Optional;

public interface OrderRepository {
    Order save(Order order);
    Optional<Order> findByStripeSessionId(String stripeSessionId);
}
