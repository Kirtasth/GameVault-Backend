package com.kirtasth.gamevault.users.domain.ports.in;

import org.springframework.web.multipart.MultipartFile;

public interface UserImageServicePort {
    String uploadAvatar(Long userId, MultipartFile avatarImage);
}
