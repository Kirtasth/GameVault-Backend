package com.kirtasth.gamevault;

import com.kirtasth.gamevault.config.ContainersConfig;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Profile("test")
@Testcontainers
@Transactional
@Import(ContainersConfig.class)
public class GameVaultApplicationTests {

    @Test
    void contextLoads() {

    }

}
