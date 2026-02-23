package com.kirtasth.gamevault.catalog.domain.ports.out;

import com.kirtasth.gamevault.catalog.application.exception.CloudinaryImageUploadException;

public interface ImageStoragePort {

    String uploadAvatar(byte[] image, String name, Long userId) throws CloudinaryImageUploadException;

    String uploadGameMainImage(byte[] image, Long gameId) throws CloudinaryImageUploadException;
}
