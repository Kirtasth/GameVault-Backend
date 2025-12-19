package com.kirtasth.gamevault.users.domain.models;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class UserIdentity {

    private Long id;

    private String loginProvider;

    private String loginProvidedUserId;

    private Instant createdAt;

}
