package com.kirtasth.gamevault.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistrar;
import org.testcontainers.containers.MinIOContainer;
import org.testcontainers.containers.PostgreSQLContainer;

@TestConfiguration(proxyBeanMethods = false)
public class ContainersConfig {

    @Bean
    @ServiceConnection
    @SuppressWarnings("resource")
    PostgreSQLContainer<?> postgres() {
        return new PostgreSQLContainer<>("postgres:16-alpine")
                .withDatabaseName("testdb")
                .withUsername("test")
                .withPassword("test");
    }

    @Bean
    @SuppressWarnings("resource")
    MinIOContainer minio() {
        return new MinIOContainer("minio/minio")
                .withUserName("test")
                .withPassword("test1234")
                .withReuse(true);
    }

    @Bean
    DynamicPropertyRegistrar minioProperties(MinIOContainer minio, PostgreSQLContainer<?> postgres) {
        return registry -> {
            registry.add("spring.datasource.url", postgres::getJdbcUrl);
            registry.add("spring.datasource.username", postgres::getUsername);
            registry.add("spring.datasource.password", postgres::getPassword);
            registry.add("spring.flyway.user", postgres::getUsername);
            registry.add("spring.flyway.password", postgres::getPassword);
            registry.add("spring.flyway.url", postgres::getJdbcUrl);
            registry.add("minio.url.internal", minio::getS3URL);
            registry.add("minio.access-key", minio::getUserName);
            registry.add("minio.secret-key", minio::getPassword);
            registry.add("minio.bucket-name", () -> "gamevault-test");
        };
    }

}