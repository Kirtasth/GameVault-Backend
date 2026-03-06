package com.kirtasth.gamevault.common.infrastructure.adapters;

import com.kirtasth.gamevault.common.domain.ports.out.ImageStoragePort;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("test")
public class CloudinaryMock implements ImageStoragePort {
    @Override
    public String uploadAvatar(byte[] image, Long userId) {
        return "";
    }

    @Override
    public String uploadGameMainImage(byte[] image, Long gameId) {
        return "";
    }
}
