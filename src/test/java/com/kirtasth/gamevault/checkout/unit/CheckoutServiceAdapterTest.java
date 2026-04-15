package com.kirtasth.gamevault.checkout.unit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kirtasth.gamevault.cart.application.exceptions.EmptyCartException;
import com.kirtasth.gamevault.cart.domain.models.CartItem;
import com.kirtasth.gamevault.cart.domain.models.ShoppingCart;
import com.kirtasth.gamevault.cart.domain.ports.in.CartServicePort;
import com.kirtasth.gamevault.catalog.domain.models.Game;
import com.kirtasth.gamevault.checkout.application.CheckoutServiceAdapter;
import com.kirtasth.gamevault.checkout.domain.models.GameKey;
import com.kirtasth.gamevault.checkout.domain.ports.out.CatalogPort;
import com.kirtasth.gamevault.checkout.domain.ports.out.GameKeyRepository;
import com.kirtasth.gamevault.checkout.domain.ports.out.StripePort;
import com.kirtasth.gamevault.checkout.domain.ports.out.StripeSessionPort;
import com.kirtasth.gamevault.checkout.infrastructure.adapters.StripeAdapterFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CheckoutServiceAdapterTest {

    @Mock
    private CartServicePort cartServicePort;

    @Mock
    private CatalogPort catalogPort;

    @Mock
    private StripeSessionPort stripeSessionPort;

    @Mock
    private StripeAdapterFactory stripeAdapterFactory;

    @Mock
    private GameKeyRepository gameKeyRepository;

    @Mock
    private ObjectMapper objectMapper;

    private CheckoutServiceAdapter checkoutServiceAdapter;

    @BeforeEach
    void setUp() {
        checkoutServiceAdapter = new CheckoutServiceAdapter(
                cartServicePort,
                catalogPort,
                stripeSessionPort,
                stripeAdapterFactory,
                gameKeyRepository,
                objectMapper,
                30L
        );
    }

    @Test
    void createCheckoutSession_ShouldReturnUrl_WhenCartIsNotEmpty() {
        Long userId = 1L;
        CartItem item = CartItem.builder().gameId(10L).quantity(1).build();
        ShoppingCart cart = ShoppingCart.builder()
                .id(100L)
                .userId(userId)
                .items(List.of(item))
                .build();
        Game game = Game.builder().id(10L).title("Test Game").price(19.99).build();
        GameKey key = GameKey.builder().id(1L).gameId(10L).keyValue("KEY-1").build();

        when(cartServicePort.getCart(userId)).thenReturn(cart);
        when(catalogPort.findGamesByIds(List.of(10L))).thenReturn(List.of(game));
        when(gameKeyRepository.findAvailableKeysByGameId(eq(10L), eq(1))).thenReturn(List.of(key));
        when(stripeSessionPort.createCheckoutSession(eq(cart), any())).thenReturn("https://stripe.com/session");

        String url = checkoutServiceAdapter.createCheckoutSession(userId);

        assertEquals("https://stripe.com/session", url);
        verify(gameKeyRepository).saveAll(anyList());
    }

    @Test
    void createCheckoutSession_ShouldThrowException_WhenCartIsEmpty() {
        Long userId = 1L;
        ShoppingCart cart = ShoppingCart.builder()
                .id(100L)
                .userId(userId)
                .items(Collections.emptyList())
                .build();

        when(cartServicePort.getCart(userId)).thenReturn(cart);

        assertThrows(EmptyCartException.class, () -> checkoutServiceAdapter.createCheckoutSession(userId));
    }

    @Test
    void handleStripeEvent_ShouldCallAdapter_WhenAdapterExists() throws JsonProcessingException {
        // Arrange
        String payload = "{\"type\": \"checkout.session.completed\"}";
        String sigHeader = "sig";
        String eventType = "checkout.session.completed";

        JsonNode rootNode = mock(JsonNode.class);
        JsonNode typeNode = mock(JsonNode.class);
        when(objectMapper.readTree(payload)).thenReturn(rootNode);
        when(rootNode.get("type")).thenReturn(typeNode);
        when(typeNode.asText()).thenReturn(eventType);

        StripePort adapter = mock(StripePort.class);
        when(stripeAdapterFactory.getAdapter(eventType)).thenReturn(Optional.of(adapter));

        // Act
        checkoutServiceAdapter.handleStripeEvent(payload, sigHeader);

        // Assert
        verify(adapter).handleEvent(payload, sigHeader);
    }

    @Test
    void handleStripeEvent_ShouldNotThrow_WhenAdapterDoesNotExist() throws JsonProcessingException {
        // Arrange
        String payload = "{\"type\": \"unknown.event\"}";
        String sigHeader = "sig";
        String eventType = "unknown.event";

        JsonNode rootNode = mock(JsonNode.class);
        JsonNode typeNode = mock(JsonNode.class);
        when(objectMapper.readTree(payload)).thenReturn(rootNode);
        when(rootNode.get("type")).thenReturn(typeNode);
        when(typeNode.asText()).thenReturn(eventType);

        when(stripeAdapterFactory.getAdapter(eventType)).thenReturn(Optional.empty());

        // Act
        checkoutServiceAdapter.handleStripeEvent(payload, sigHeader);

        // Assert
        verifyNoInteractions(stripeSessionPort);
    }
}
