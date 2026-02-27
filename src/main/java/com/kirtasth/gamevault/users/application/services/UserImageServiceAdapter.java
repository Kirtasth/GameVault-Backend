package com.kirtasth.gamevault.users.application.services;

import com.kirtasth.gamevault.common.domain.ports.out.ImageStoragePort;
import com.kirtasth.gamevault.common.infrastructure.exception.CloudinaryImageUploadException;
import com.kirtasth.gamevault.users.domain.ports.in.UserImageServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class UserImageServiceAdapter implements UserImageServicePort {

    private final ImageStoragePort imageStorage;

    @Override
    public String uploadAvatar(Long userId, MultipartFile avatarImage) {
        if (avatarImage != null && !avatarImage.isEmpty()) {
            try {
                return imageStorage.uploadAvatar(avatarImage.getBytes(), userId);
            } catch (IOException e) {
                throw new CloudinaryImageUploadException("/users/" + userId + "/avatar");
            }
        }
        return null;
    }
}
