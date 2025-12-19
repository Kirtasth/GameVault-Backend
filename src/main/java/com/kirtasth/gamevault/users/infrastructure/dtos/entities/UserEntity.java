package com.kirtasth.gamevault.users.infrastructure.dtos.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users", schema = "auth")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(name = "email_verified", nullable = false)
    private boolean emailVerified;

    @Column(name = "account_enabled", nullable = false)
    private boolean accountEnabled;

    @Column(name = "account_expired", nullable = false)
    private boolean accountExpired;

    @Column(name = "account_locked", nullable = false)
    private boolean accountLocked;

    @Column(name = "credentials_expired", nullable = false)
    private boolean credentialsExpired;

    @Column(name = "lock_reason")
    private String lockReason;

    @Column(name = "lock_instant")
    private Instant lockInstant;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private Instant createdAt;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private Instant updatedAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "deleted_at")
    private Instant deletedAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<UserIdentityEntity> identities;
}
