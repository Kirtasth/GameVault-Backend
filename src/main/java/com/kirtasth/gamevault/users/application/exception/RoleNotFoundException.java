package com.kirtasth.gamevault.users.application.exception;

import com.kirtasth.gamevault.common.infrastructure.exception.ApplicationException;
import com.kirtasth.gamevault.common.models.enums.RoleEnum;

public class RoleNotFoundException extends ApplicationException {
    public RoleNotFoundException(RoleEnum roleEnum) {
        super("Role with enum: " + roleEnum.name() + " not found.");
    }
}
