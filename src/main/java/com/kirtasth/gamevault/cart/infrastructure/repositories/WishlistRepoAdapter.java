package com.kirtasth.gamevault.cart.infrastructure.repositories;

import com.kirtasth.gamevault.cart.domain.models.Wishlist;
import com.kirtasth.gamevault.cart.domain.ports.out.WishlistRepoPort;
import com.kirtasth.gamevault.cart.infrastructure.dtos.entities.WishlistKey;
import com.kirtasth.gamevault.cart.infrastructure.mappers.WishlistMapper;
import com.kirtasth.gamevault.cart.infrastructure.repositories.jpa.WishlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class WishlistRepoAdapter implements WishlistRepoPort {

    private final WishlistRepository wishlistRepository;
    private final WishlistMapper wishlistMapper;

    @Override
    public Wishlist save(Wishlist wishlist) {
        var entity = wishlistMapper.toEntity(wishlist);
        return wishlistMapper.toDomain(wishlistRepository.save(entity));
    }

    @Override
    public List<Wishlist> findByUserId(Long userId) {
        return wishlistRepository.findByUserId(userId).stream()
                .map(wishlistMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long userId, Long gameId) {
        wishlistRepository.deleteById(new WishlistKey(userId, gameId));
    }

}
