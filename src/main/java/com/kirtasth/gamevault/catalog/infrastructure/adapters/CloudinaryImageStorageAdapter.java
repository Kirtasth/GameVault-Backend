package com.kirtasth.gamevault.catalog.infrastructure.adapters;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.kirtasth.gamevault.catalog.domain.ports.out.ImageStoragePort;
import com.kirtasth.gamevault.common.models.util.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CloudinaryImageStorageAdapter implements ImageStoragePort {

    private final Cloudinary cloudinary;

    @Override
    public Result<String> upload(byte[] image, String name) {
        try {
            var uploadResult = cloudinary.uploader().upload(image, ObjectUtils.asMap("public_id", name));
            return new Result.Success<>((String) uploadResult.get("url"));
        } catch (IOException e) {
            return new Result.Failure<>(500, "Error uploading image", null, e);
        }
    }

    @Override
    public Result<String> uploadAvatar(byte[] image, String name, Long userId) {

        try {
            var options = ObjectUtils.asMap(
                    "folder", "gamevault/users/" + userId,
                    "public_id", "avatar",
                    "overwrite", true,
                    "context", "user_id=" + userId,
                    "tags", "user_avatar",
                    "upload_preset", "avatar_preset"
            );

            var uploadResult = cloudinary.uploader().upload(image, options);
            return new Result.Success<>(uploadResult.get("secure_url").toString());

        } catch (IOException e) {
            return new Result.Failure<>(500, "Error uploading image", null, e);
        }
    }

    @Override
    public Result<String> uploadGameMainImage(byte[] image, String name, Long gameId) {

        try {
            var options = ObjectUtils.asMap(
                    "folder", "gamevault/games/" + gameId,
                    "public_id", "main_image",
                    "overwrite", true,
                    "context", "game_id=" + gameId,
                    "tags", "game_main_image"
            );

            var uploadResult = cloudinary.uploader().upload(image, options);
            return new Result.Success<>(uploadResult.get("secure_url").toString());

        } catch (IOException e) {
            return new Result.Failure<>(500, "Error uploading image", null, e);
        }
    }


}
