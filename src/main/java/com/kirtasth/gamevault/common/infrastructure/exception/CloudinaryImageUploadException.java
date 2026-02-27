package com.kirtasth.gamevault.common.infrastructure.exception;

public class CloudinaryImageUploadException extends InternalServerException {
    public CloudinaryImageUploadException(String path) {
        super("Could not upload image to path: " + path + ".");
    }
}
