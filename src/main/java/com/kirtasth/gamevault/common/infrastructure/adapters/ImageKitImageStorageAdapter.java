package com.kirtasth.gamevault.common.infrastructure.adapters;

import com.kirtasth.gamevault.common.application.exception.ImageUploadException;
import com.kirtasth.gamevault.common.domain.ports.out.ImageStoragePort;   
import io.imagekit.client.ImageKitClient;
import io.imagekit.models.files.FileUploadParams;
import io.imagekit.models.files.FileUploadResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@RequiredArgsConstructor
@Profile("pro")
public class ImageKitImageStorageAdapter implements ImageStoragePort {    

    private final ImageKitClient imageKit;

    @Override
    public String uploadAvatar(MultipartFile image, Long userId) {        
        try {
            FileUploadParams params = FileUploadParams.builder()
                    .file(image.getBytes())
                    .fileName("avatar")
                    .folder("users/" + userId)
                    .useUniqueFileName(false)
                    .build();

            FileUploadResponse response = imageKit.files().upload(params);
            return response.url().orElseThrow(() -> new ImageUploadException("ImageKit response did not contain a URL"));
        } catch (Exception e) {
            throw new ImageUploadException("Failed to upload avatar to ImageKit: " + e.getMessage());
        }
    }

    @Override
    public String uploadGameMainImage(byte[] image, Long gameId) {        
        try {
            FileUploadParams params = FileUploadParams.builder()
                    .file(image)
                    .fileName("main")
                    .folder("games/" + gameId)
                    .useUniqueFileName(false)
                    .build();

            FileUploadResponse response = imageKit.files().upload(params);
            return response.url().orElseThrow(() -> new ImageUploadException("ImageKit response did not contain a URL"));
        } catch (Exception e) {
            throw new ImageUploadException("Failed to upload game image to ImageKit: " + e.getMessage());
        }
    }
}
