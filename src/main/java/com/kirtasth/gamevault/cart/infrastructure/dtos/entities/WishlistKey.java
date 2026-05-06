package com.kirtasth.gamevault.cart.infrastructure.dtos.entities;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class WishlistKey implements Serializable {
    private Long userId;
    private Long gameId;
}
