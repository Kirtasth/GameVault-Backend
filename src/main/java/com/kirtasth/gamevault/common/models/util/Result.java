package com.kirtasth.gamevault.common.models.util;

import com.fasterxml.jackson.databind.node.ObjectNode;

public sealed interface Result<T> permits Result.Success, Result.Failure {

    record Success<T> (T data) implements Result<T>{}

    record Failure<T> (
        int errorCode,
        String errorMsg,
        ObjectNode errorDetails,
        Exception exception
    ) implements Result<T> {}
}
