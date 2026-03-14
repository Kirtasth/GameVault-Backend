package com.kirtasth.gamevault.config;

import com.kirtasth.gamevault.common.domain.ports.out.ImageStoragePort;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.PostgreSQLContainer;

@TestConfiguration( proxyBeanMethods = false)
public class ContainersConfig {

    @Autowired
    PostgreSQLContainer<?> postgres;

    @MockitoBean
    ImageStoragePort imageStoragePort;

    @Bean
    @ServiceConnection
    @SuppressWarnings("resource")
    static PostgreSQLContainer<?> postgres() {
        return new PostgreSQLContainer<>("postgres:16")
                .withDatabaseName("testdb")
                .withUsername("test")
                .withPassword("test");
    }

    @PostConstruct
    void configureProperties() {
        System.setProperty("spring.datasource.url", postgres.getJdbcUrl());
        System.setProperty("spring.datasource.username", postgres.getUsername());
        System.setProperty("spring.datasource.password", postgres.getPassword());
    }

}