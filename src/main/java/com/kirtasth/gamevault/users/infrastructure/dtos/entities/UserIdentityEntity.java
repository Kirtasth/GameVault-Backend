package com.kirtasth.gamevault.users.infrastructure.dtos.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "users_identities",
        schema = "auth",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "unique_provider_provider_id",
                        columnNames = { "provider", "provider_user_id" }
                ),
                @UniqueConstraint(
                        name = "unique_user_provider",
                        columnNames = { "users_id", "provider" }
                )
        }
)
public class UserIdentityEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "users_id", nullable = false)
    private UserEntity user;

    @Column(name = "login_provider", nullable = false)
    private String loginProvider;

    @Column(name = "login_provided_user_id", nullable = false)
    private String loginProvidedUserId;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at",nullable = false, updatable = false)
    private Instant createdAt;
}
