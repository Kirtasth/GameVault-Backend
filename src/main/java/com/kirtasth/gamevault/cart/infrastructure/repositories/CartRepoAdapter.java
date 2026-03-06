package com.kirtasth.gamevault.cart.infrastructure.repositories;

import com.kirtasth.gamevault.cart.application.exceptions.CartItemNotFoundException;
import com.kirtasth.gamevault.cart.application.exceptions.ShoppingCartUpdateException;
import com.kirtasth.gamevault.cart.domain.models.CartItem;
import com.kirtasth.gamevault.cart.domain.models.ShoppingCart;
import com.kirtasth.gamevault.cart.domain.ports.out.CartRepoPort;
import com.kirtasth.gamevault.cart.infrastructure.dtos.entities.ShoppingCartEntity;
import com.kirtasth.gamevault.cart.infrastructure.mappers.CartMapper;
import com.kirtasth.gamevault.cart.infrastructure.repositories.jpa.CartItemRepository;
import com.kirtasth.gamevault.cart.infrastructure.repositories.jpa.ShoppingCartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CartRepoAdapter implements CartRepoPort {

    private final ShoppingCartRepository shoppingCartRepository;
    private final CartItemRepository cartItemRepository;
    private final CartMapper cartMapper;

    @Override
    public ShoppingCart save(ShoppingCart cart) {
        ShoppingCartEntity entity = cartMapper.toEntity(cart);
        try {
            return cartMapper.toDomain(shoppingCartRepository.save(entity));
        } catch (DataIntegrityViolationException e) {
            throw new ShoppingCartUpdateException(cart.userId());
        }
    }

    @Override
    public CartItem findById(Long itemId) {
        return cartMapper.toDomain(cartItemRepository.findById(itemId).orElseThrow(
                () -> new CartItemNotFoundException(itemId)
        ));
    }

    @Override
    public Optional<ShoppingCart> findOpenedByUserId(Long userId) {
        return shoppingCartRepository.findFirstOpenedByUserId(userId)
                .map(cartMapper::toDomain);
    }

    @Override
    public CartItem save(CartItem item) {
        return cartMapper.toDomain(cartItemRepository.save(cartMapper.toEntity(item)));
    }

}
