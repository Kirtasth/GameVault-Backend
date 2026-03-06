package com.kirtasth.gamevault.users.application.exception;

import com.kirtasth.gamevault.common.application.exception.ResourceNotFoundException;
import com.kirtasth.gamevault.common.domain.models.enums.RoleEnum;

public class RoleNotFoundException extends ResourceNotFoundException {
    public RoleNotFoundException(RoleEnum roleEnum) {
        super("Role with enum: " + roleEnum.name() + " not found.");
    }
}
