package com.kirtasth.gamevault.cart.infrastructure.dtos.entities;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Getter;

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
