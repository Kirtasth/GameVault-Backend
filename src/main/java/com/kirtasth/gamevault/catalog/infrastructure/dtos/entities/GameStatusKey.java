package com.kirtasth.gamevault.catalog.infrastructure.dtos.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class GameStatusKey implements Serializable {

    @Column(name = "game_id")
    private Long gameId;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Override
    public int hashCode() {
        return Objects.hash(gameId, createdAt);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        GameStatusKey other = (GameStatusKey) obj;
        return Objects.equals(gameId, other.gameId) && Objects.equals(createdAt, other.createdAt);
    }
}
