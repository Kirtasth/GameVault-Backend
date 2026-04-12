package com.kirtasth.gamevault.checkout.infrastructure.dtos.entities;

import com.kirtasth.gamevault.catalog.infrastructure.dtos.entities.GameEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "game_keys", schema = "checkout")
public class GameKeyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    private GameEntity game;

    @Column(name = "key_value", nullable = false, unique = true)
    private String keyValue;

    @Column(name = "used_at")
    private Instant usedAt;

    @Column(name = "order_item_id")
    private Long orderItemId; // Made nullable for stock keys

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;
}
