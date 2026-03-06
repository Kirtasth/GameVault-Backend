package com.kirtasth.gamevault.common.infrastructure.adapters;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.kirtasth.gamevault.common.application.exception.CloudinaryImageUploadException;
import com.kirtasth.gamevault.common.domain.ports.out.ImageStoragePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CloudinaryImageStorageAdapter implements ImageStoragePort {

    private final Cloudinary cloudinary;

    @Override
    public String uploadAvatar(byte[] image, Long userId) {
        try {
            var options = ObjectUtils.asMap(
                    "folder", "gamevault/users/" + userId,
                    "public_id", "avatar",
                    "overwrite", true,
                    "context", "user_id=" + userId,
                    "tags", "user_avatar"
            );

            var uploadResult = cloudinary.uploader().upload(image, options);
            return uploadResult.get("secure_url").toString();
        } catch (IOException e) {
            throw new CloudinaryImageUploadException("/users/" + userId + "/avatar");
        }
    }

    @Override
    public String uploadGameMainImage(byte[] image, Long gameId) {
        try {
            var options = ObjectUtils.asMap(
                    "folder", "gamevault/games/" + gameId,
                    "public_id", "main_image",
                    "overwrite", true,
                    "context", "game_id=" + gameId,
                    "tags", "game_main_image"
            );

            var uploadResult = cloudinary.uploader().upload(image, options);
            return uploadResult.get("secure_url").toString();
        } catch (IOException e) {
            throw new CloudinaryImageUploadException("/games/" + gameId + "/main_image");
        }
    }
}
