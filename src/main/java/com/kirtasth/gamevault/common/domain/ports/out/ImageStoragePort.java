package com.kirtasth.gamevault.common.domain.ports.out;

import org.springframework.web.multipart.MultipartFile;

public interface ImageStoragePort {

    String uploadAvatar(MultipartFile image, Long userId);

    String uploadGameMainImage(byte[] image, Long gameId);
}
