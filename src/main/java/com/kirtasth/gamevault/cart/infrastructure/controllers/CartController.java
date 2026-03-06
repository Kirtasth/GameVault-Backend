package com.kirtasth.gamevault.cart.infrastructure.controllers;

import com.kirtasth.gamevault.cart.domain.ports.in.CartServicePort;
import com.kirtasth.gamevault.cart.infrastructure.dtos.requests.AddItemPetitionRequest;
import com.kirtasth.gamevault.cart.infrastructure.dtos.responses.CartResponse;
import com.kirtasth.gamevault.cart.infrastructure.mappers.CartMapper;
import com.kirtasth.gamevault.users.domain.models.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartServicePort cartService;
    private final CartMapper cartMapper;

    @GetMapping
    public ResponseEntity<CartResponse> getCart(Authentication authentication) {
        var userId = ((AuthUser) authentication.getPrincipal()).getId();
        var cart = cartService.getCart(userId);
        return ResponseEntity.ok(cartMapper.toCartResponse(cart));
    }

    @PostMapping
    public ResponseEntity<CartResponse> addItemToCart(
            @RequestBody AddItemPetitionRequest addItemPetitionRequest,
            Authentication authentication) {
        var userId = ((AuthUser) authentication.getPrincipal()).getId();
        var cart = cartService.addItemToCart(userId, cartMapper.toAddItemPetition(addItemPetitionRequest));
        return ResponseEntity.ok(cartMapper.toCartResponse(cart));
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<CartResponse> removeItemFromCart(@PathVariable Long itemId, Authentication authentication) {
        var userId = ((AuthUser) authentication.getPrincipal()).getId();
        var cart = cartService.removeItemFromCart(userId, itemId);
        return ResponseEntity.ok(cartMapper.toCartResponse(cart));
    }

    @DeleteMapping
    public ResponseEntity<Void> clearCart(Authentication authentication) {
        var userId = ((AuthUser) authentication.getPrincipal()).getId();
        cartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }
}
