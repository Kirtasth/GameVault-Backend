package com.kirtasth.gamevault.users.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kirtasth.gamevault.config.ContainersConfig;
import com.kirtasth.gamevault.users.infrastructure.dtos.requests.CredentialsRequest;
import com.kirtasth.gamevault.users.infrastructure.dtos.requests.NewUserRequest;
import com.kirtasth.gamevault.users.infrastructure.dtos.requests.RefreshTokenPetitionRequest;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.Duration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@SpringBootTest
@AutoConfigureMockMvc
@Import(ContainersConfig.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuthControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @Order(1)
    void shouldRegisterUser() throws Exception {
        NewUserRequest request = new NewUserRequest();
        request.setUsername("testuser");
        request.setEmail("test@example.com");
        request.setPassword("password123");

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @Order(2)
    void shouldLoginUser() throws Exception {
        // First register the user
        NewUserRequest registerRequest = new NewUserRequest();
        registerRequest.setUsername("loginuser");
        registerRequest.setEmail("login@example.com");
        registerRequest.setPassword("password123");

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());

        // Then login
        CredentialsRequest loginRequest = new CredentialsRequest();
        loginRequest.setEmail("login@example.com");
        loginRequest.setPassword("password123");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk());
    }

    @Test
    @Order(3)
    void shouldFailLoginWithInvalidCredentials() throws Exception {
        CredentialsRequest loginRequest = new CredentialsRequest();
        loginRequest.setEmail("nonexistent@example.com");
        loginRequest.setPassword("wrongpassword");

        // Spring Security usually returns 401 for bad credentials when using DaoAuthenticationProvider
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(4)
    void shouldRefreshToken() throws Exception {
        // First register
        NewUserRequest registerRequest = new NewUserRequest();
        registerRequest.setUsername("refreshuser");
        registerRequest.setEmail("refresh@example.com");
        registerRequest.setPassword("password123");

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());

        // Then login to get tokens
        CredentialsRequest loginRequest = new CredentialsRequest();
        loginRequest.setEmail("refresh@example.com");
        loginRequest.setPassword("password123");

        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        // We wait so the login and refresh does not happen at the same milisecond
        await().pollDelay(Duration.ofMillis(1000)).until(() -> true);

        String loginResponse = result.getResponse().getContentAsString();

        JsonNode jsonNode = objectMapper.readTree(loginResponse);
        String refreshToken = jsonNode.get("refreshToken").asText();

        // Use refresh token
        RefreshTokenPetitionRequest refreshRequest = new RefreshTokenPetitionRequest();
        refreshRequest.setRefreshToken(refreshToken);

        mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().isOk());
    }

    @Test
    @Order(5)
    void shouldValidateToken() throws Exception {
        // Register and login to get token
        NewUserRequest registerRequest = new NewUserRequest();
        registerRequest.setUsername("validateuser");
        registerRequest.setEmail("validate@example.com");
        registerRequest.setPassword("password123");

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());

        CredentialsRequest loginRequest = new CredentialsRequest();
        loginRequest.setEmail("validate@example.com");
        loginRequest.setPassword("password123");

        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String loginResponse = result.getResponse().getContentAsString();

        JsonNode jsonNode = objectMapper.readTree(loginResponse);
        String accessToken = jsonNode.get("accessToken").asText();

        // Validate token
        mockMvc.perform(get("/api/v1/auth")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());
    }

    @Test
    @Order(6)
    void shouldLogout() throws Exception {
        // Register and login to get token
        NewUserRequest registerRequest = new NewUserRequest();
        registerRequest.setUsername("logoutuser");
        registerRequest.setEmail("logout@example.com");
        registerRequest.setPassword("password123");

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());

        CredentialsRequest loginRequest = new CredentialsRequest();
        loginRequest.setEmail("logout@example.com");
        loginRequest.setPassword("password123");

        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String loginResponse = result.getResponse().getContentAsString();

        JsonNode jsonNode = objectMapper.readTree(loginResponse);
        String accessToken = jsonNode.get("accessToken").asText();

        // Logout
        mockMvc.perform(post("/api/v1/auth/logout")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());
    }
}