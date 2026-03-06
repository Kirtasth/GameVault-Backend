package com.kirtasth.gamevault.cart.domain.ports.in;

import com.kirtasth.gamevault.cart.domain.models.Wishlist;

import java.util.List;

public interface WishlistServicePort {
    List<Wishlist> getWishlist(Long userId);
    Wishlist addToWishlist(Long userId, Long gameId);
    void removeFromWishlist(Long userId, Long gameId);
}
