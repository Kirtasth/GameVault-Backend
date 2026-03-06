package com.kirtasth.gamevault.cart.infrastructure.controllers;

import com.kirtasth.gamevault.cart.domain.ports.in.WishlistServicePort;
import com.kirtasth.gamevault.cart.infrastructure.dtos.responses.WishlistResponse;
import com.kirtasth.gamevault.cart.infrastructure.mappers.WishlistMapper;
import com.kirtasth.gamevault.users.domain.models.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistServicePort wishlistService;
    private final WishlistMapper wishlistMapper;

    @GetMapping
    public ResponseEntity<List<WishlistResponse>> getWishlist(Authentication authentication) {
        var userId = ((AuthUser) authentication.getPrincipal()).getId();
        var wishlist = wishlistService.getWishlist(userId);
        return ResponseEntity.ok(wishlist.stream().map(wishlistMapper::toWishlistResponse).toList());
    }

    @PostMapping("/{gameId}")
    public ResponseEntity<WishlistResponse> addToWishlist(@PathVariable Long gameId, Authentication authentication) {
        var userId = ((AuthUser) authentication.getPrincipal()).getId();
        var wishlist = wishlistService.addToWishlist(userId, gameId);
        return ResponseEntity.ok(wishlistMapper.toWishlistResponse(wishlist));
    }

    @DeleteMapping("/{gameId}")
    public ResponseEntity<Void> removeFromWishlist(@PathVariable Long gameId, Authentication authentication) {
        var userId = ((AuthUser) authentication.getPrincipal()).getId();
        wishlistService.removeFromWishlist(userId, gameId);
        return ResponseEntity.noContent().build();
    }
}
