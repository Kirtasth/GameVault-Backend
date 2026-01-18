package com.kirtasth.gamevault.users.domain.models;


import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;


@Getter()
@Setter
public class User {

    private Long id;

    private String username;

    private String email;

    private String password;

    private String avatarUrl;

    private String bio;

    private boolean emailVerified;

    private boolean accountEnabled;

    private boolean accountExpired;

    private boolean accountLocked;

    private boolean credentialsExpired;

    private String lockReason;

    private Instant lockInstant;

    private Instant createdAt;

    private Instant updatedAt;

    private Instant deletedAt;

    private List<Role> roles;


}
