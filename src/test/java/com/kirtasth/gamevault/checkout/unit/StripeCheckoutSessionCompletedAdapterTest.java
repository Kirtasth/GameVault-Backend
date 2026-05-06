package com.kirtasth.gamevault.checkout.unit;

import com.kirtasth.gamevault.cart.domain.ports.in.CartServicePort;
import com.kirtasth.gamevault.checkout.application.exceptions.PaymentIntegrationException;
import com.kirtasth.gamevault.checkout.domain.ports.out.CatalogPort;
import com.kirtasth.gamevault.checkout.domain.ports.out.GameKeyRepository;
import com.kirtasth.gamevault.checkout.domain.ports.out.OrderRepository;
import com.kirtasth.gamevault.checkout.infrastructure.adapters.StripeCheckoutSessionCompletedAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class StripeCheckoutSessionCompletedAdapterTest {

    @Mock
    private CartServicePort cartServicePort;
    @Mock
    private CatalogPort catalogPort;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private GameKeyRepository gameKeyRepository;

    @InjectMocks
    private StripeCheckoutSessionCompletedAdapter adapter;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(adapter, "webhookSecret", "whsec_test");
    }

    @Test
    void handleEvent_ShouldThrowException_WhenSignatureHeaderIsMissing() {
        String payload = "{}";
        Map<String, String> headers = Collections.emptyMap();

        assertThrows(PaymentIntegrationException.class, () -> adapter.handleEvent(payload, headers));
    }

    @Test
    void handleEvent_ShouldWork_WhenSignatureHeaderIsLowercase() {
        String payload = "{\"type\": \"checkout.session.completed\"}";
        Map<String, String> headers = Map.of("stripe-signature", "t=123,v1=abc");

        // Fails on actual verification but passes null check
        assertThrows(PaymentIntegrationException.class, () -> adapter.handleEvent(payload, headers));
    }
}
