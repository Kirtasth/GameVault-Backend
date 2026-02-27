package com.kirtasth.gamevault.users.application.exception;

import com.kirtasth.gamevault.common.infrastructure.exception.InternalServerException;
import com.kirtasth.gamevault.common.models.enums.RoleEnum;
import org.apache.logging.log4j.util.Strings;

import java.util.List;

public class RoleAssignmentException extends InternalServerException {
    public RoleAssignmentException(Long userId, List<RoleEnum> roles) {
        super("Could not add some or all of the roles(" + Strings.join(roles, ',') + ") to user with id: " + userId + ".");
    }
}
