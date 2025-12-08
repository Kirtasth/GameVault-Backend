package com.kirtasth.gamevault.users.domain;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class UserIdentity {

    private Long id;

    private String provider;

    private String providedUserId;

    private Instant createdAt;

}
