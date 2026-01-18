package com.kirtasth.gamevault.common.models.util;

import java.util.Map;

public sealed interface Result<T> permits Result.Success, Result.Failure {

    record Success<T> (T data) implements Result<T>{}

    record Failure<T> (
        int errorCode,
        String errorMsg,
        Map<String, String> errorDetails,
        Exception exception
    ) implements Result<T> {}
}
