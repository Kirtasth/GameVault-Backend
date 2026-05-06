package com.kirtasth.gamevault.common.infrastructure.config;

import io.imagekit.client.ImageKitClient;
import io.imagekit.client.okhttp.ImageKitOkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("pro")
public class ImageKitConfig {

    @Bean
    public ImageKitClient imageKitClient() {
        return ImageKitOkHttpClient.fromEnv();
    }
}
