package com.kirtasth.gamevault.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistrar;
import org.testcontainers.containers.MinIOContainer;
import org.testcontainers.containers.PostgreSQLContainer;

@TestConfiguration(proxyBeanMethods = false)
public class ContainersConfig {

    @SuppressWarnings("resource")
    static final PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>("postgres:16-alpine")
                    .withDatabaseName("testdb")
                    .withUsername("test")
                    .withPassword("test")
                    .withReuse(true);

    static final MinIOContainer MINIO =
            new MinIOContainer("minio/minio")
                    .withUserName("test")
                    .withPassword("test1234")
                    .withReuse(true);

    static {
        POSTGRES.start();
        MINIO.start();
    }

    @Bean
    @ServiceConnection
    PostgreSQLContainer<?> postgres() {
        return POSTGRES;
    }

    @Bean
    MinIOContainer minio() {
        return MINIO;
    }

    @Bean
    DynamicPropertyRegistrar minioProperties() {
        return registry -> {
            registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
            registry.add("spring.datasource.username", POSTGRES::getUsername);
            registry.add("spring.datasource.password", POSTGRES::getPassword);
            registry.add("spring.flyway.user", POSTGRES::getUsername);
            registry.add("spring.flyway.password", POSTGRES::getPassword);
            registry.add("spring.flyway.url", POSTGRES::getJdbcUrl);
            registry.add("minio.url.internal", () -> "http://%s:%d".formatted(
                    MINIO.getHost(),
                    MINIO.getFirstMappedPort()));
            registry.add("minio.access-key", MINIO::getUserName);
            registry.add("minio.secret-key", MINIO::getPassword);
            registry.add("minio.bucket-name", () -> "gamevault-test");
        };
    }
}