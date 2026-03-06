package com.kirtasth.gamevault.cart.application;

import com.kirtasth.gamevault.cart.domain.models.AddItemPetition;
import com.kirtasth.gamevault.cart.domain.models.CartItem;
import com.kirtasth.gamevault.cart.domain.models.ShoppingCart;
import com.kirtasth.gamevault.cart.domain.ports.in.CartServicePort;
import com.kirtasth.gamevault.cart.domain.ports.out.CartRepoPort;
import com.kirtasth.gamevault.cart.domain.ports.out.CatalogPort;
import com.kirtasth.gamevault.catalog.domain.models.Game;
import com.kirtasth.gamevault.common.domain.models.enums.CartStatusEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceAdapter implements CartServicePort {

    private final CartRepoPort cartRepoPort;
    private final CatalogPort catalogPort;

    @Override
    public ShoppingCart getCart(Long userId) {
        return cartRepoPort.findOpenedByUserId(userId)
                .orElseGet(() -> createNewCart(userId));
    }

    @Override
    public ShoppingCart addItemToCart(Long userId, AddItemPetition addItemPetition) {
        Game game = catalogPort.getGame(addItemPetition.gameId());
        ShoppingCart cart = getCart(userId);

        List<CartItem> currentItems = cart.items() != null ? new ArrayList<>(cart.items()) : new ArrayList<>();
        Optional<CartItem> existingItem = currentItems.stream()
                .filter(item -> Objects.equals(item.gameId(), game.id()))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            CartItem updatedItem = CartItem.builder()
                    .id(item.id())
                    .cartId(item.cartId())
                    .gameId(item.gameId())
                    .quantity(addItemPetition.quantity())
                    .priceAtAddition(item.priceAtAddition())
                    .createdAt(item.createdAt())
                    .updatedAt(item.updatedAt())
                    .build();
            currentItems.remove(item);
            currentItems.add(updatedItem);
        } else {
            CartItem newItem = CartItem.builder()
                    .cartId(cart.id())
                    .gameId(game.id())
                    .quantity(addItemPetition.quantity())
                    .priceAtAddition(game.price())
                    .build();
            currentItems.add(newItem);
        }

        double currentTotal = cart.totalPrice() != null ? cart.totalPrice() : 0.0;
        double addedPrice = game.price() * addItemPetition.quantity();

        ShoppingCart updatedCart = ShoppingCart.builder()
                .id(cart.id())
                .userId(cart.userId())
                .items(currentItems)
                .status(cart.status())
                .totalPrice(currentTotal + addedPrice)
                .createdAt(cart.createdAt())
                .updatedAt(cart.updatedAt())
                .build();

        return cartRepoPort.save(updatedCart);
    }

    @Override
    public ShoppingCart removeItemFromCart(Long userId, Long itemId) {
        CartItem cartItem = cartRepoPort.findById(itemId);
        ShoppingCart cart = getCart(userId);

        List<CartItem> updatedItems = cart.items() != null
                ? new ArrayList<>(cart.items())
                : new ArrayList<>();
        var prizeToRemove = cartItem.priceAtAddition() * cartItem.quantity();
        updatedItems.removeIf(item -> Objects.equals(item.id(), itemId));

        double currentTotal = cart.totalPrice() != null ? cart.totalPrice() : 0.0;

        ShoppingCart updatedCart = ShoppingCart.builder()
                .id(cart.id())
                .userId(cart.userId())
                .items(updatedItems)
                .status(cart.status())
                .totalPrice(currentTotal - prizeToRemove)
                .createdAt(cart.createdAt())
                .updatedAt(cart.updatedAt())
                .build();

        return cartRepoPort.save(updatedCart);
    }

    @Override
    public void clearCart(Long userId) {
        ShoppingCart cart = getCart(userId);
        if (cart.items() == null || cart.items().isEmpty()) {
            return;
        }
        var clearedCart = ShoppingCart.builder()
                .id(cart.id())
                .userId(cart.userId())
                .items(new ArrayList<>())
                .totalPrice(0.0)
                .status(cart.status())
                .createdAt(cart.createdAt())
                .updatedAt(cart.updatedAt())
                .build();

        cartRepoPort.save(clearedCart);
    }

    private ShoppingCart createNewCart(Long userId) {
        ShoppingCart cart = ShoppingCart.builder()
                .userId(userId)
                .items(new ArrayList<>())
                .status(CartStatusEnum.OPENED)
                .totalPrice(0.0)
                .build();
        return cartRepoPort.save(cart);
    }
}
