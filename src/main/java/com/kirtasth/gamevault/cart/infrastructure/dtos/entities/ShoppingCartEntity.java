package com.kirtasth.gamevault.cart.infrastructure.dtos.entities;

import com.kirtasth.gamevault.common.domain.models.enums.CartStatusEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "shopping_carts", schema = "cart")
public class ShoppingCartEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @OneToMany(mappedBy = "shoppingCart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItemEntity> items;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private CartStatusEnum status;

    @Column(name = "total_price", nullable = false)
    @JdbcTypeCode(SqlTypes.NUMERIC)
    private Double totalPrice;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
