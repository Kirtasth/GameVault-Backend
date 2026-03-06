package com.kirtasth.gamevault.cart.application;

import com.kirtasth.gamevault.cart.domain.models.Wishlist;
import com.kirtasth.gamevault.cart.domain.ports.in.WishlistServicePort;
import com.kirtasth.gamevault.cart.domain.ports.out.WishlistRepoPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class WishlistServiceAdapter implements WishlistServicePort {

    private final WishlistRepoPort wishlistRepoPort;

    @Override
    public List<Wishlist> getWishlist(Long userId) {
        return wishlistRepoPort.findByUserId(userId);
    }

    @Override
    public Wishlist addToWishlist(Long userId, Long gameId) {
        Wishlist wishlist = Wishlist.builder()
                .userId(userId)
                .gameId(gameId)
                .build();
        return wishlistRepoPort.save(wishlist);
    }

    @Override
    public void removeFromWishlist(Long userId, Long gameId) {
        wishlistRepoPort.delete(userId, gameId);
    }
}
