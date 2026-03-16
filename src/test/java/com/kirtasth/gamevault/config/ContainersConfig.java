package com.kirtasth.gamevault.config;

import com.kirtasth.gamevault.common.domain.ports.out.ImageStoragePort;
import jakarta.annotation.PostConstruct;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.testcontainers.containers.PostgreSQLContainer;

@TestConfiguration(proxyBeanMethods = false)
public class ContainersConfig {

    @Autowired
    PostgreSQLContainer<?> postgres;

    @Bean
    @ServiceConnection
    @SuppressWarnings("resource")
    static PostgreSQLContainer<?> postgres() {
        return new PostgreSQLContainer<>("postgres:16")
                .withDatabaseName("testdb")
                .withUsername("test")
                .withPassword("test");
    }

    @Bean
    @Primary
    public ImageStoragePort imageStoragePort() {
        var mock = Mockito.mock(ImageStoragePort.class);
        Mockito.when(mock.uploadGameMainImage(Mockito.any(), Mockito.anyLong())).thenReturn("");
        Mockito.when(mock.uploadAvatar(Mockito.any(), Mockito.anyLong())).thenReturn("");

        return mock;
    }

    @PostConstruct
    void configureProperties() {
        System.setProperty("spring.datasource.url", postgres.getJdbcUrl());
        System.setProperty("spring.datasource.username", postgres.getUsername());
        System.setProperty("spring.datasource.password", postgres.getPassword());
    }

}