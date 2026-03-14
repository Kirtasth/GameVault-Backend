package com.kirtasth.gamevault;

import com.kirtasth.gamevault.config.ContainersConfig;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(ContainersConfig.class)
public class GameVaultApplicationTests {

    @Test
    void contextLoads() {

    }

}
