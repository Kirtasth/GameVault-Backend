package com.kirtasth.gamevault.users.domain;

import com.kirtasth.gamevault.common.models.enums.RoleEnum;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class Role {

    private Long id;

    private RoleEnum role;

    private String description;

    private Instant createdAt;

    private Instant updatedAt;

    private Instant deletedAt;
}
