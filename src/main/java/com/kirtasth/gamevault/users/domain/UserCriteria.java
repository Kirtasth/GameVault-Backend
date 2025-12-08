package com.kirtasth.gamevault.users.domain;

import com.kirtasth.gamevault.common.models.enums.RoleEnum;
import lombok.*;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class UserCriteria {

    private String username;

    private String email;

    private Boolean emailVerified;

    private Boolean accountEnabled;

    private Boolean accountExpired;

    private Boolean accountLocked;

    private Boolean credentialsExpired;

    private Instant lockedBefore;

    private Instant lockedAfter;

    private Instant createdBefore;

    private Instant createdAfter;

    private Instant updatedBefore;

    private Instant updatedAfter;

    private Instant deletedBefore;

    private Instant deletedAfter;

    private List<RoleEnum> roleEnums;

}
