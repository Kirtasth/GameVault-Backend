package com.kirtasth.gamevault.catalog.infrastructure.dtos.entities;


import com.kirtasth.gamevault.common.models.enums.GameStatusEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "game_statuses", schema = "catalog")
public class GameStatusEntity {

    @EmbeddedId
    private GameStatusKey id;

    @ManyToOne
    @MapsId("gameId")
    @JoinColumn(name = "game_id")
    private GameEntity game;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private GameStatusEnum status;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "deleted_at")
    private Instant deletedAt;

    @PrePersist
    public void prePersist() {
        if (id == null) {
            id = new GameStatusKey();
        }
        if (id.getCreatedAt() == null) {
            id.setCreatedAt(Instant.now());
        }
    }
}
