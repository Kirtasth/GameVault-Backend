package com.kirtasth.gamevault;

import com.kirtasth.gamevault.config.ContainersConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@Import({ContainersConfig.class})
@ActiveProfiles("test")
public class GameVaultApplicationTests {

    @Test
    void contextLoads() {

    }

}
