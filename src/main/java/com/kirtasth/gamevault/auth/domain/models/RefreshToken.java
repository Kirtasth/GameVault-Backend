package com.kirtasth.gamevault.auth.domain.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RefreshToken {

    private Long id;
    private Long userId;
    private String token;
    private Instant createdAt;
    private Instant revokedAt;
}
