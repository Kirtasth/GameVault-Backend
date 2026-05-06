package com.kirtasth.gamevault.config;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.SetBucketPolicyArgs;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistrar;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;

@TestConfiguration(proxyBeanMethods = false)
public class ContainersConfig {

    private static final String BUCKET_NAME = "gamevault-test";

    @SuppressWarnings("resource")
    static final PostgreSQLContainer<?> POSTGRES_CONTAINER =
            new PostgreSQLContainer<>("postgres:16-alpine")
                    .withDatabaseName("testdb")
                    .withUsername("test")
                    .withPassword("test1234");

    static final GenericContainer<?> MINIO =
            new GenericContainer<>(DockerImageName.parse("cgr.dev/chainguard/minio:latest"))
                    .withEnv("MINIO_ROOT_USER", "test")
                    .withEnv("MINIO_ROOT_PASSWORD", "test1234")
                    .withCommand("server", "/data", "--console-address", ":9001")
                    .withExposedPorts(9000, 9001)
                    .waitingFor(Wait.forLogMessage(".*API: http://.*", 1))
                    .withStartupTimeout(Duration.ofSeconds(120));

    static {
        POSTGRES_CONTAINER.start();
        MINIO.start();
        initializeMinio();
    }

    private static String getResolvedHost(GenericContainer<?> container) {
        String host = container.getHost();
        return "host.docker.internal".equals(host) ? "127.0.0.1" : host;
    }

    private static void initializeMinio() {
        try {
            String host = getResolvedHost(MINIO);
            String url = "http://%s:%d".formatted(host, MINIO.getMappedPort(9000));

            MinioClient minioClient = MinioClient.builder()
                    .endpoint(url)
                    .credentials("test", "test1234")
                    .build();

            if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(BUCKET_NAME).build())) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(BUCKET_NAME).build());
            }

            // Set bucket policy to public read
            String policy = """
                    {
                        "Version": "2012-10-17",
                        "Statement": [
                            {
                                "Effect": "Allow",
                                "Principal": {"AWS": ["*"]},
                                "Action": ["s3:GetObject"],
                                "Resource": ["arn:aws:s3:::%s/*"]
                            }
                        ]
                    }
                    """.formatted(BUCKET_NAME);
            minioClient.setBucketPolicy(SetBucketPolicyArgs.builder().bucket(BUCKET_NAME).config(policy).build());

        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize MinIO bucket", e);
        }
    }

    @Bean
    PostgreSQLContainer<?> postgres() {
        return POSTGRES_CONTAINER;
    }

    @Bean
    GenericContainer<?> minio() {
        return MINIO;
    }

    @Bean
    DynamicPropertyRegistrar testProperties() {
        return registry -> {
            String pgHost = getResolvedHost(POSTGRES_CONTAINER);
            int pgPort = POSTGRES_CONTAINER.getMappedPort(5432);
            String pgUrl = "jdbc:postgresql://%s:%d/testdb?loggerLevel=OFF".formatted(pgHost, pgPort);

            registry.add("spring.datasource.url", () -> pgUrl);
            registry.add("spring.datasource.username", POSTGRES_CONTAINER::getUsername);
            registry.add("spring.datasource.password", POSTGRES_CONTAINER::getPassword);
            registry.add("spring.flyway.url", () -> pgUrl);
            registry.add("spring.flyway.user", POSTGRES_CONTAINER::getUsername);
            registry.add("spring.flyway.password", POSTGRES_CONTAINER::getPassword);

            String minioHost = getResolvedHost(MINIO);
            String minioUrl = "http://%s:%d".formatted(minioHost, MINIO.getMappedPort(9000));
            registry.add("minio.url.internal", () -> minioUrl);
            registry.add("minio.access-key", () -> "test");
            registry.add("minio.secret-key", () -> "test1234");
            registry.add("minio.bucket-name", () -> BUCKET_NAME);
        };
    }
}