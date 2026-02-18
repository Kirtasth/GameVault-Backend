package com.kirtasth.gamevault.catalog.domain.ports.out;

import com.kirtasth.gamevault.common.models.util.Result;

public interface ImageStoragePort {
    Result<String> upload(byte[] image, String name);

    Result<String> uploadAvatar(byte[] image, String name, Long userId);

    Result<String> uploadGameMainImage(byte[] image, String name, Long gameId);
}
