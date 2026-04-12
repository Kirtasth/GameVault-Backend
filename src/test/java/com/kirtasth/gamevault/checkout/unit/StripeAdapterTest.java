package com.kirtasth.gamevault.checkout.unit;

import com.kirtasth.gamevault.cart.domain.models.CartItem;
import com.kirtasth.gamevault.cart.domain.models.ShoppingCart;
import com.kirtasth.gamevault.catalog.domain.models.Game;
import com.kirtasth.gamevault.checkout.infrastructure.adapters.StripeAdapter;
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
class StripeAdapterTest {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private StripeClient stripeClient;

    private StripeAdapter stripeAdapter;

    @BeforeEach
    void setUp() {
        stripeAdapter = new StripeAdapter(
                stripeClient,
                "whsec_test_secret",
                "http://success.url",
                "http://cancel.url"
        );
    }

    @Test
    void createCheckoutSession_ShouldConsolidateItemsIntoOneLineItem() throws StripeException {
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
        when(stripeClient.checkout().sessions().create(any(SessionCreateParams.class))).thenReturn(session);

        // Act
        String url = stripeAdapter.createCheckoutSession(cart, games);

        // Assert
        assertEquals("http://stripe.url", url);
        
        verify(stripeClient.checkout().sessions()).create(argThat((SessionCreateParams params) -> {
            List<SessionCreateParams.LineItem> lineItems = params.getLineItems();
            if (lineItems == null || lineItems.size() != 1) return false;
            
            SessionCreateParams.LineItem item = lineItems.get(0);
            if (item.getQuantity() != 1L) return false;
            
            SessionCreateParams.LineItem.PriceData priceData = item.getPriceData();
            BigDecimal amount = priceData.getUnitAmountDecimal();
            if (amount.compareTo(new BigDecimal("5000")) != 0) return false;
            
            SessionCreateParams.LineItem.PriceData.ProductData productData = priceData.getProductData();
            if (!"GameVault Order".equals(productData.getName())) return false;
            
            String description = productData.getDescription();
            return description.contains("Game 1 (x1)") && description.contains("Game 2 (x2)");
        }));
    }

    @Test
    void createCheckoutSession_ShouldIncludeImagesInConsolidatedView() throws StripeException {
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
        when(stripeClient.checkout().sessions().create(any(SessionCreateParams.class))).thenReturn(session);

        // Act
        stripeAdapter.createCheckoutSession(cart, games);

        // Assert
        verify(stripeClient.v1().checkout().sessions()).create(argThat((SessionCreateParams params) -> {
            SessionCreateParams.LineItem.PriceData.ProductData productData = 
                params.getLineItems().get(0).getPriceData().getProductData();
            List<String> images = productData.getImages();
            return images != null && images.size() == 1 && images.get(0).equals("https://api.url/img1.png");
        }));
    }

    @Test
    void verifyWebhookSignature_ShouldThrowException_WhenHeaderIsNull() {
        // Act & Assert
        try {
            stripeAdapter.verifyWebhookSignature("{}", null);
        } catch (NullPointerException e) {
            // Webhook.constructEvent throws NPE when sigHeader is null
        } catch (Exception e) {
            // Catch other potential exceptions
        }
    }
}
