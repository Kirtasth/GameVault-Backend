package com.kirtasth.gamevault.common.domain.ports.out;

public interface ImageStoragePort {

    String uploadAvatar(byte[] image, Long userId);

    String uploadGameMainImage(byte[] image, Long gameId);
}
