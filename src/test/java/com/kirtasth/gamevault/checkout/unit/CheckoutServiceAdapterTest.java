package com.kirtasth.gamevault.checkout.unit;

import com.kirtasth.gamevault.cart.application.exceptions.EmptyCartException;
import com.kirtasth.gamevault.cart.domain.models.CartItem;
import com.kirtasth.gamevault.cart.domain.models.ShoppingCart;
import com.kirtasth.gamevault.cart.domain.ports.in.CartServicePort;
import com.kirtasth.gamevault.catalog.domain.models.Game;
import com.kirtasth.gamevault.checkout.application.CheckoutServiceAdapter;
import com.kirtasth.gamevault.checkout.domain.models.GameKey;
import com.kirtasth.gamevault.checkout.domain.models.Order;
import com.kirtasth.gamevault.checkout.domain.models.OrderItem;
import com.kirtasth.gamevault.checkout.domain.ports.out.CatalogPort;
import com.kirtasth.gamevault.checkout.domain.ports.out.GameKeyRepository;
import com.kirtasth.gamevault.checkout.domain.ports.out.OrderRepository;
import com.kirtasth.gamevault.checkout.domain.ports.out.StripePort;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.checkout.Session;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

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
    private StripePort stripePort;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private GameKeyRepository gameKeyRepository;

    @InjectMocks
    private CheckoutServiceAdapter checkoutServiceAdapter;

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

        when(cartServicePort.getCart(userId)).thenReturn(cart);
        when(catalogPort.findGamesByIds(List.of(10L))).thenReturn(List.of(game));
        when(stripePort.createCheckoutSession(eq(cart), any())).thenReturn("https://stripe.com/session");

        String url = checkoutServiceAdapter.createCheckoutSession(userId);

        assertEquals("https://stripe.com/session", url);
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
    void handleStripeWebhook_ShouldIgnore_WhenEventIsNotCheckoutSessionCompleted() {
        String payload = "payload";
        String sigHeader = "sig";
        Event event = mock(Event.class);

        when(stripePort.verifyWebhookSignature(payload, sigHeader)).thenReturn(event);
        when(event.getType()).thenReturn("payment_intent.succeeded");

        checkoutServiceAdapter.handleStripeWebhook(payload, sigHeader);

        verify(cartServicePort, never()).getCart(any());
    }

    @Test
    void handleStripeWebhook_ShouldLogAndReturn_WhenUserIdMissingInMetadata() {
        // Arrange
        String payload = "payload";
        String sigHeader = "sig";
        Event event = mock(Event.class);
        EventDataObjectDeserializer deserializer = mock(EventDataObjectDeserializer.class);
        Session session = mock(Session.class);

        when(stripePort.verifyWebhookSignature(payload, sigHeader)).thenReturn(event);
        when(event.getType()).thenReturn("checkout.session.completed");
        when(event.getDataObjectDeserializer()).thenReturn(deserializer);
        when(deserializer.getObject()).thenReturn(Optional.of(session));
        when(session.getMetadata()).thenReturn(Collections.emptyMap());

        // Act
        checkoutServiceAdapter.handleStripeWebhook(payload, sigHeader);

        // Assert
        verify(cartServicePort, never()).getCart(any());
        verify(orderRepository, never()).save(any());
    }

    @Test
    void handleStripeWebhook_ShouldLogAndReturn_WhenCartIsEmpty() {
        // Arrange
        String payload = "payload";
        String sigHeader = "sig";
        Event event = mock(Event.class);
        EventDataObjectDeserializer deserializer = mock(EventDataObjectDeserializer.class);
        Session session = mock(Session.class);

        when(stripePort.verifyWebhookSignature(payload, sigHeader)).thenReturn(event);
        when(event.getType()).thenReturn("checkout.session.completed");
        when(event.getDataObjectDeserializer()).thenReturn(deserializer);
        when(deserializer.getObject()).thenReturn(Optional.of(session));
        when(session.getMetadata()).thenReturn(Map.of("user_id", "1"));

        Long userId = 1L;
        ShoppingCart emptyCart = ShoppingCart.builder().userId(userId).items(Collections.emptyList()).build();
        when(cartServicePort.getCart(userId)).thenReturn(emptyCart);

        // Act
        checkoutServiceAdapter.handleStripeWebhook(payload, sigHeader);

        // Assert
        verify(catalogPort, never()).findGamesByIds(any());
        verify(orderRepository, never()).save(any());
    }

    @Test
    void handleStripeWebhook_ShouldSkipItem_WhenGameNotFoundInCatalog() {
        // Arrange
        String payload = "payload";
        String sigHeader = "sig";
        Event event = mock(Event.class);
        EventDataObjectDeserializer deserializer = mock(EventDataObjectDeserializer.class);
        Session session = mock(Session.class);

        when(stripePort.verifyWebhookSignature(payload, sigHeader)).thenReturn(event);
        when(event.getType()).thenReturn("checkout.session.completed");
        when(event.getDataObjectDeserializer()).thenReturn(deserializer);
        when(deserializer.getObject()).thenReturn(Optional.of(session));
        when(session.getMetadata()).thenReturn(Map.of("user_id", "1"));
        when(session.getId()).thenReturn("cs_test_123");

        Long userId = 1L;
        CartItem item = CartItem.builder().gameId(10L).quantity(1).build();
        ShoppingCart cart = ShoppingCart.builder().userId(userId).items(List.of(item)).build();
        when(cartServicePort.getCart(userId)).thenReturn(cart);

        // Game 10 is NOT returned by catalogPort
        when(catalogPort.findGamesByIds(anyList())).thenReturn(Collections.emptyList());

        Order mockOrder = Order.builder()
                .id(100L)
                .items(new ArrayList<>())
                .build();
        when(orderRepository.save(any(Order.class))).thenReturn(mockOrder);

        // Act
        checkoutServiceAdapter.handleStripeWebhook(payload, sigHeader);

        // Assert
        verify(orderRepository).save(argThat(order -> order.getItems().isEmpty()));
        verify(gameKeyRepository, never()).findRandomUnusedKeyByGameId(anyLong());
        verify(cartServicePort).clearCart(userId);
    }

    @Test
    void handleStripeWebhook_ShouldLogErrorButContinue_WhenNoGameKeyAvailable() {
        // Arrange
        String payload = "payload";
        String sigHeader = "sig";
        Event event = mock(Event.class);
        EventDataObjectDeserializer deserializer = mock(EventDataObjectDeserializer.class);
        Session session = mock(Session.class);

        when(stripePort.verifyWebhookSignature(payload, sigHeader)).thenReturn(event);
        when(event.getType()).thenReturn("checkout.session.completed");
        when(event.getDataObjectDeserializer()).thenReturn(deserializer);
        when(deserializer.getObject()).thenReturn(Optional.of(session));
        when(session.getMetadata()).thenReturn(Map.of("user_id", "1"));
        when(session.getId()).thenReturn("cs_test_123");

        Long userId = 1L;
        CartItem item = CartItem.builder().gameId(10L).quantity(1).build();
        ShoppingCart cart = ShoppingCart.builder().userId(userId).items(List.of(item)).build();
        when(cartServicePort.getCart(userId)).thenReturn(cart);

        Game game = Game.builder().id(10L).price(10.0).build();
        when(catalogPort.findGamesByIds(anyList())).thenReturn(List.of(game));

        Order mockOrder = Order.builder()
                .id(100L)
                .items(new ArrayList<>(List.of(OrderItem.builder().id(200L).gameId(10L).build())))
                .build();
        when(orderRepository.save(any(Order.class))).thenReturn(mockOrder);

        // No key available
        when(gameKeyRepository.findRandomUnusedKeyByGameId(10L)).thenReturn(Optional.empty());

        // Act
        checkoutServiceAdapter.handleStripeWebhook(payload, sigHeader);

        // Assert
        verify(gameKeyRepository, never()).save(any());
        verify(cartServicePort).clearCart(userId);
    }

    @Test
    void handleStripeWebhook_ShouldHandleComplexCartWithMultipleItemsAndQuantities() {
        // Arrange
        String payload = "payload";
        String sigHeader = "sig";
        Event event = mock(Event.class);
        EventDataObjectDeserializer deserializer = mock(EventDataObjectDeserializer.class);
        Session session = mock(Session.class);

        when(stripePort.verifyWebhookSignature(payload, sigHeader)).thenReturn(event);
        when(event.getType()).thenReturn("checkout.session.completed");
        when(event.getDataObjectDeserializer()).thenReturn(deserializer);
        when(deserializer.getObject()).thenReturn(Optional.of(session));
        when(session.getMetadata()).thenReturn(Map.of("user_id", "1"));
        when(session.getId()).thenReturn("cs_test_123");

        Long userId = 1L;
        CartItem item1 = CartItem.builder().gameId(10L).quantity(2).build();
        CartItem item2 = CartItem.builder().gameId(20L).quantity(1).build();
        ShoppingCart cart = ShoppingCart.builder().userId(userId).items(List.of(item1, item2)).build();
        when(cartServicePort.getCart(userId)).thenReturn(cart);

        Game game1 = Game.builder().id(10L).price(10.0).build();
        Game game2 = Game.builder().id(20L).price(20.0).build();
        when(catalogPort.findGamesByIds(anyList())).thenReturn(List.of(game1, game2));

        OrderItem orderItem1 = OrderItem.builder().id(201L).gameId(10L).build();
        OrderItem orderItem2 = OrderItem.builder().id(202L).gameId(10L).build();
        OrderItem orderItem3 = OrderItem.builder().id(203L).gameId(20L).build();
        Order mockOrder = Order.builder()
                .id(100L)
                .items(new ArrayList<>(List.of(orderItem1, orderItem2, orderItem3)))
                .build();
        when(orderRepository.save(any(Order.class))).thenReturn(mockOrder);

        when(gameKeyRepository.findRandomUnusedKeyByGameId(10L)).thenReturn(Optional.of(new GameKey()));
        when(gameKeyRepository.findRandomUnusedKeyByGameId(20L)).thenReturn(Optional.of(new GameKey()));

        // Act
        checkoutServiceAdapter.handleStripeWebhook(payload, sigHeader);

        // Assert
        verify(orderRepository).save(argThat(order -> order.getItems().size() == 3));
        verify(gameKeyRepository, Mockito.times(3)).findRandomUnusedKeyByGameId(anyLong());
        verify(gameKeyRepository, Mockito.times(3)).save(any());
        verify(cartServicePort).clearCart(userId);
    }
}
