package com.kirtasth.gamevault.cart.domain.ports.out;

import com.kirtasth.gamevault.cart.domain.models.Wishlist;

import java.util.List;

public interface WishlistRepoPort {
    Wishlist save(Wishlist wishlist);
    List<Wishlist> findByUserId(Long userId);
    void delete(Long userId, Long gameId);
}
