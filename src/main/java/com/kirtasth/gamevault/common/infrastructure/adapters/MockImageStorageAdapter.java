package com.kirtasth.gamevault.common.infrastructure.adapters;

import com.kirtasth.gamevault.common.domain.ports.out.ImageStoragePort;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@Profile("test")
public class MockImageStorageAdapter implements ImageStoragePort {


    @Override
    public String uploadAvatar(MultipartFile image, Long userId) {
        return "";
    }

    @Override
    public String uploadGameMainImage(byte[] image, Long gameId) {
        return "";
    }
}
