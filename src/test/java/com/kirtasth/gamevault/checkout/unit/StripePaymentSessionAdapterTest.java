package com.kirtasth.gamevault.checkout.unit;

import com.kirtasth.gamevault.cart.domain.models.CartItem;
import com.kirtasth.gamevault.cart.domain.models.ShoppingCart;
import com.kirtasth.gamevault.catalog.domain.models.Game;
import com.kirtasth.gamevault.checkout.infrastructure.adapters.StripePaymentSessionAdapter;
import com.stripe.StripeClient;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StripePaymentSessionAdapterTest {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private StripeClient stripeClient;

    private StripePaymentSessionAdapter stripePaymentSessionAdapter;

    @BeforeEach
    void setUp() {
        stripePaymentSessionAdapter = new StripePaymentSessionAdapter(
                stripeClient,
                "http://success.url",
                "http://cancel.url",
                30L
        );
    }

    @Test
    void createCheckoutSession_ShouldAddIndividualLineItems() throws StripeException {
        // Arrange
        Game game1 = Game.builder()
                .id(1L)
                .title("Game 1")
                .price(10.0)
                .build();
        Game game2 = Game.builder()
                .id(2L)
                .title("Game 2")
                .price(20.0)
                .build();

        CartItem item1 = CartItem.builder().gameId(1L).quantity(1).build();
        CartItem item2 = CartItem.builder().gameId(2L).quantity(2).build();

        ShoppingCart cart = ShoppingCart.builder()
                .id(100L)
                .userId(10L)
                .items(List.of(item1, item2))
                .totalPrice(50.0)
                .build();

        Map<Long, Game> games = Map.of(1L, game1, 2L, game2);

        Session session = mock(Session.class);
        when(session.getUrl()).thenReturn("http://stripe.url");
        when(stripeClient.v1().checkout().sessions().create(any(SessionCreateParams.class))).thenReturn(session);

        // Act
        String url = stripePaymentSessionAdapter.createCheckoutSession(cart, games);

        // Assert
        assertEquals("http://stripe.url", url);
        
        verify(stripeClient.v1().checkout().sessions()).create(argThat((SessionCreateParams params) -> {
            List<SessionCreateParams.LineItem> lineItems = params.getLineItems();
            if (lineItems == null || lineItems.size() != 2) return false;
            
            // Check submit type and payment method
            if (!SessionCreateParams.SubmitType.PAY.equals(params.getSubmitType())) return false;
            if (!params.getPaymentMethodTypes().contains(SessionCreateParams.PaymentMethodType.CARD)) return false;

            // Check first item
            SessionCreateParams.LineItem stripeItem1 = lineItems.get(0);
            if (stripeItem1.getQuantity() != 1L) return false;
            if (stripeItem1.getPriceData().getUnitAmountDecimal().compareTo(new BigDecimal("1000")) != 0) return false;
            if (!"Game 1".equals(stripeItem1.getPriceData().getProductData().getName())) return false;

            // Check second item
            SessionCreateParams.LineItem stripeItem2 = lineItems.get(1);
            if (stripeItem2.getQuantity() != 2L) return false;
            if (stripeItem2.getPriceData().getUnitAmountDecimal().compareTo(new BigDecimal("2000")) != 0) return false;
            if (!"Game 2".equals(stripeItem2.getPriceData().getProductData().getName())) return false;
            
            return true;
        }));
    }

    @Test
    void createCheckoutSession_ShouldNotIncludeImages() throws StripeException {
        // Arrange
        Game game1 = Game.builder()
                .id(1L)
                .title("Game 1")
                .price(10.0)
                .imageUrl("https://api.url/img1.png")
                .build();

        CartItem item1 = CartItem.builder().gameId(1L).quantity(1).build();

        ShoppingCart cart = ShoppingCart.builder()
                .id(100L)
                .userId(10L)
                .items(List.of(item1))
                .totalPrice(10.0)
                .build();

        Map<Long, Game> games = Map.of(1L, game1);

        Session session = mock(Session.class);
        when(session.getUrl()).thenReturn("http://stripe.url");
        when(stripeClient.v1().checkout().sessions().create(any(SessionCreateParams.class))).thenReturn(session);

        // Act
        stripePaymentSessionAdapter.createCheckoutSession(cart, games);

        // Assert
        verify(stripeClient.v1().checkout().sessions()).create(argThat((SessionCreateParams params) -> {
            SessionCreateParams.LineItem.PriceData.ProductData productData = 
                params.getLineItems().get(0).getPriceData().getProductData();
            List<String> images = productData.getImages();
            return images == null || images.isEmpty();
        }));
    }
}
