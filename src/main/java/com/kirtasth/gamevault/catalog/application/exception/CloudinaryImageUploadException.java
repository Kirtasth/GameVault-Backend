package com.kirtasth.gamevault.catalog.application.exception;

import com.kirtasth.gamevault.common.infrastructure.exception.ApplicationException;

public class CloudinaryImageUploadException extends ApplicationException {
    public CloudinaryImageUploadException(String path) {
        super("Could not upload image to path: " + path + ".");
    }
}
