package com.kirtasth.gamevault.cart.infrastructure.dtos.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "wishlists", schema = "cart")
@IdClass(WishlistKey.class)
public class WishlistEntity {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @Id
    @Column(name = "game_id")
    private Long gameId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
