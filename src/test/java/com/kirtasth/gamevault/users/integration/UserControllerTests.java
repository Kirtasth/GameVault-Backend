package com.kirtasth.gamevault.users.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kirtasth.gamevault.config.ContainersConfig;
import com.kirtasth.gamevault.users.infrastructure.dtos.requests.CredentialsRequest;
import com.kirtasth.gamevault.users.infrastructure.dtos.requests.NewUserRequest;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(ContainersConfig.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String accessToken;
    private Long userId;

    @BeforeAll
    void setup() throws Exception {
        // Register user
        NewUserRequest registerRequest = new NewUserRequest();
        registerRequest.setUsername("usertest");
        registerRequest.setEmail("usertest@example.com");
        registerRequest.setPassword("password123");

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());

        // Login to get token
        CredentialsRequest loginRequest = new CredentialsRequest();
        loginRequest.setEmail("usertest@example.com");
        loginRequest.setPassword("password123");

        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String responseString = result.getResponse().getContentAsString();
        JsonNode responseJson = objectMapper.readTree(responseString);
        accessToken = responseJson.get("accessToken").asText();
        userId = responseJson.get("userId").asLong();
    }

    @Test
    @Order(1)
    void shouldGetUserInfo() throws Exception {
        mockMvc.perform(get("/api/v1/users/" + userId)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());
    }

    @Test
    @Order(2)
    void shouldFailListingUsers() throws Exception {
        mockMvc.perform(get("/api/v1/users")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(3)
    void shouldUpdateUser() throws Exception {
        MockMultipartFile avatar = new MockMultipartFile("avatarImage", "avatar.png", MediaType.IMAGE_PNG_VALUE, "avatar".getBytes());

        mockMvc.perform(multipart("/api/v1/users/" + userId)
                        .file(avatar)
                        .param("username", "updateduser")
                        .param("email", "updateduser@example.com")
                        .with(request -> {
                            request.setMethod("PUT");
                            request.addHeader("Authorization", "Bearer " + accessToken);
                            return request;
                        }))
                .andExpect(status().isOk());
    }
}
