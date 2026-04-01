package com.kirtasth.gamevault.cart.unit;

import com.kirtasth.gamevault.cart.application.CartServiceAdapter;
import com.kirtasth.gamevault.cart.domain.models.AddItemPetition;
import com.kirtasth.gamevault.cart.domain.models.CartItem;
import com.kirtasth.gamevault.cart.domain.models.ShoppingCart;
import com.kirtasth.gamevault.cart.domain.ports.out.CartRepoPort;
import com.kirtasth.gamevault.cart.domain.ports.out.CatalogPort;
import com.kirtasth.gamevault.catalog.domain.models.Game;
import com.kirtasth.gamevault.common.domain.models.enums.CartStatusEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceAdapterTest {

    @Mock
    private CartRepoPort cartRepoPort;

    @Mock
    private CatalogPort catalogPort;

    @InjectMocks
    private CartServiceAdapter cartServiceAdapter;

    @Test
    void getCart_ShouldReturnOpenedCart_WhenCartExists() {
        ShoppingCart cart = ShoppingCart.builder()
                .id(1L)
                .userId(1L)
                .status(CartStatusEnum.OPENED)
                .build();
        when(cartRepoPort.findOpenedByUserId(1L)).thenReturn(Optional.of(cart));

        ShoppingCart result = cartServiceAdapter.getCart(1L);

        assertNotNull(result);
        assertEquals(1L, result.id());
    }

    @Test
    void getCart_ShouldCreateNewCart_WhenNoOpenedCartExists() {
        when(cartRepoPort.findOpenedByUserId(1L)).thenReturn(Optional.empty());
        when(cartRepoPort.save(any(ShoppingCart.class))).thenAnswer(invocation -> {
            ShoppingCart cart = invocation.getArgument(0);
            return ShoppingCart.builder()
                    .id(10L)
                    .userId(cart.userId())
                    .status(cart.status())
                    .totalPrice(cart.totalPrice())
                    .items(cart.items())
                    .build();
        });

        ShoppingCart result = cartServiceAdapter.getCart(1L);

        assertNotNull(result);
        assertEquals(10L, result.id());
        assertEquals(1L, result.userId());
        assertEquals(CartStatusEnum.OPENED, result.status());
        assertEquals(0.0, result.totalPrice());
    }

    @Test
    void addItemToCart_ShouldAddNewItem_WhenItemDoesNotExist() {
        Game game = Game.builder().id(100L).price(20.0).build();
        ShoppingCart cart = ShoppingCart.builder()
                .id(1L)
                .userId(1L)
                .items(new ArrayList<>())
                .totalPrice(0.0)
                .build();

        when(catalogPort.getGame(100L)).thenReturn(game);
        when(cartRepoPort.findOpenedByUserId(1L)).thenReturn(Optional.of(cart));
        when(cartRepoPort.save(any(ShoppingCart.class))).thenAnswer(i -> i.getArgument(0));

        AddItemPetition petition = new AddItemPetition(1L, 100L, 2);
        ShoppingCart result = cartServiceAdapter.addItemToCart(1L, petition);

        assertEquals(1, result.items().size());
        assertEquals(40.0, result.totalPrice());
        assertEquals(100L, result.items().getFirst().gameId());
        assertEquals(2, result.items().getFirst().quantity());
    }

    @Test
    void addItemToCart_ShouldUpdateQuantity_WhenItemExists() {
        Game game = Game.builder().id(100L).price(20.0).build();
        CartItem existingItem = CartItem.builder().id(50L).gameId(100L).quantity(1).priceAtAddition(20.0).build();
        List<CartItem> items = new ArrayList<>();
        items.add(existingItem);
        ShoppingCart cart = ShoppingCart.builder()
                .id(1L)
                .userId(1L)
                .items(items)
                .totalPrice(20.0)
                .build();

        when(catalogPort.getGame(100L)).thenReturn(game);
        when(cartRepoPort.findOpenedByUserId(1L)).thenReturn(Optional.of(cart));
        when(cartRepoPort.save(any(ShoppingCart.class))).thenAnswer(i -> i.getArgument(0));

        AddItemPetition petition = new AddItemPetition(1L, 100L, 3);
        ShoppingCart result = cartServiceAdapter.addItemToCart(1L, petition);

        assertEquals(1, result.items().size());
        assertEquals(80.0, result.totalPrice()); // 20 (old) + 60 (added 3*20)
        assertEquals(3, result.items().getFirst().quantity());
    }

    @Test
    void removeItemFromCart_ShouldRemoveItemAndDecreasePrice() {
        CartItem itemToRemove = CartItem.builder().id(50L).gameId(100L).quantity(2).priceAtAddition(20.0).build();
        List<CartItem> items = new ArrayList<>();
        items.add(itemToRemove);
        ShoppingCart cart = ShoppingCart.builder()
                .id(1L)
                .userId(1L)
                .items(items)
                .totalPrice(40.0)
                .build();

        when(cartRepoPort.findById(50L)).thenReturn(itemToRemove);
        when(cartRepoPort.findOpenedByUserId(1L)).thenReturn(Optional.of(cart));
        when(cartRepoPort.save(any(ShoppingCart.class))).thenAnswer(i -> i.getArgument(0));

        ShoppingCart result = cartServiceAdapter.removeItemFromCart(1L, 50L);

        assertTrue(result.items().isEmpty());
        assertEquals(0.0, result.totalPrice());
    }

    @Test
    void clearCart_ShouldClearItemsAndResetPrice() {
        List<CartItem> items = new ArrayList<>();
        items.add(CartItem.builder().id(50L).build());
        ShoppingCart cart = ShoppingCart.builder()
                .id(1L)
                .userId(1L)
                .items(items)
                .totalPrice(40.0)
                .build();

        when(cartRepoPort.findOpenedByUserId(1L)).thenReturn(Optional.of(cart));
        when(cartRepoPort.save(any(ShoppingCart.class))).thenAnswer(i -> i.getArgument(0));

        cartServiceAdapter.clearCart(1L);

        verify(cartRepoPort).save((ShoppingCart) argThat(savedCart -> {
            ShoppingCart c = (ShoppingCart) savedCart;
            return c.items().isEmpty() && c.totalPrice() == 0.0;
        }));
    }
}
