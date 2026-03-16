package com.kirtasth.gamevault.config;

import com.kirtasth.gamevault.common.domain.ports.out.ImageStoragePort;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class MockImageStoragePort {

    @Bean
    @Primary
    public ImageStoragePort imageStoragePort() {
        var mock = Mockito.mock(ImageStoragePort.class);
        Mockito.when(mock.uploadGameMainImage(Mockito.any(), Mockito.anyLong())).thenReturn("");
        Mockito.when(mock.uploadAvatar(Mockito.any(), Mockito.anyLong())).thenReturn("");

        return mock;
    }
}
