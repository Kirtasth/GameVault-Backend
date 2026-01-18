package com.kirtasth.gamevault.users.infrastructure.dtos.responses;

import com.kirtasth.gamevault.users.domain.models.Role;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
public class UserResponse {

    private Long id;

    private String username;

    private String email;

    private String avatarUrl;

    private String bio;

    private boolean accountLocked;

    private String lockReason;

    private Instant lockInstant;

    private List<Role> roles;
}
