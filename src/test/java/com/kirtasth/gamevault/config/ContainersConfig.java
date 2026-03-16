package com.kirtasth.gamevault.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.MinIOContainer;
import org.testcontainers.containers.PostgreSQLContainer;

@TestConfiguration(proxyBeanMethods = false)
public class ContainersConfig {

    @Autowired
    PostgreSQLContainer<?> postgres;

    @Autowired
    MinIOContainer minio;

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
    @SuppressWarnings("resource")
    static MinIOContainer minio() {
        return new MinIOContainer("minio/minio:RELEASE.2023-09-04T19-57-37Z")
                .withUserName("test")
                .withPassword("test1234")
                .withReuse(true);
    }

    @PostConstruct
    void configureProperties() {
        System.setProperty("spring.datasource.url", postgres.getJdbcUrl());
        System.setProperty("spring.datasource.username", postgres.getUsername());
        System.setProperty("spring.datasource.password", postgres.getPassword());
        System.setProperty("spring.flyway.user", postgres.getUsername());
        System.setProperty("spring.flyway.password", postgres.getPassword());
        System.setProperty("spring.flyway.url", postgres.getJdbcUrl());
        System.setProperty("minio.url.internal", minio.getS3URL());
        System.setProperty("minio.access-key", minio.getUserName());
        System.setProperty("minio.secret-key", minio.getPassword());
        System.setProperty("minio.bucket-name", "gamevault-test");
    }

}