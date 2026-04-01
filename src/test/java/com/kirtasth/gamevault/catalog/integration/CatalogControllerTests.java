package com.kirtasth.gamevault.catalog.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.kirtasth.gamevault.catalog.infrastructure.dtos.requests.CustomGameListRequest;
import com.kirtasth.gamevault.catalog.infrastructure.dtos.requests.NewDeveloperRequest;
import com.kirtasth.gamevault.common.BaseIntegrationTest;
import com.kirtasth.gamevault.users.infrastructure.dtos.requests.CredentialsRequest;
import com.kirtasth.gamevault.users.infrastructure.dtos.requests.NewUserRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CatalogControllerTests extends BaseIntegrationTest {

    private String accessToken;

    @BeforeEach
    void setUp() throws Exception {
        // Register user
        NewUserRequest registerRequest = new NewUserRequest();
        registerRequest.setUsername("cataloguser");
        registerRequest.setEmail("cataloguser@example.com");
        registerRequest.setPassword("password123");

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());

        // Login to get token
        CredentialsRequest loginRequest = new CredentialsRequest();
        loginRequest.setEmail("cataloguser@example.com");
        loginRequest.setPassword("password123");

        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String responseString = result.getResponse().getContentAsString();
        JsonNode responseJson = objectMapper.readTree(responseString);
        accessToken = responseJson.get("accessToken").asText();
    }

    @Test
    @Order(1)
    void shouldRegisterDeveloper() throws Exception {
        NewDeveloperRequest newDeveloperRequest = new NewDeveloperRequest("DevName", "DevDescription");

        mockMvc.perform(post("/api/v1/catalog/developer")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newDeveloperRequest)))
                .andExpect(status().isCreated());
    }

    @Test
    @Order(2)
    void shouldCreateNewGameAsDeveloper() throws Exception {
        // First register as dev
        NewDeveloperRequest newDeveloperRequest = new NewDeveloperRequest("DevName", "DevDescription");
        mockMvc.perform(post("/api/v1/catalog/developer")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newDeveloperRequest)))
                .andExpect(status().isCreated());

        MockMultipartFile image = new MockMultipartFile("image", "game.png", MediaType.IMAGE_PNG_VALUE, "image".getBytes());

        mockMvc.perform(multipart("/api/v1/catalog")
                        .file(image)
                        .param("title", "Integration Test Game")
                        .param("description", "A great game")
                        .param("price", "29.99")
                        .param("releaseDate", "2023-12-01T10:15:30Z")
                        .with(request -> {
                            request.setMethod("POST");
                            request.addHeader("Authorization", "Bearer " + accessToken);
                            return request;
                        }))
                .andExpect(status().isCreated());
    }

    @Test
    @Order(3)
    void shouldFailToCreateNewGameAsUser() throws Exception {
        MockMultipartFile image = new MockMultipartFile("image", "game.png", MediaType.IMAGE_PNG_VALUE, "image".getBytes());

        mockMvc.perform(multipart("/api/v1/catalog")
                        .file(image)
                        .param("title", "Integration Test Game 2")
                        .param("description", "A great game")
                        .param("price", "29.99")
                        .param("releaseDate", "2023-12-01T10:15:30")
                        .with(request -> {
                            request.setMethod("POST");
                            request.addHeader("Authorization", "Bearer " + accessToken);
                            return request;
                        }))
                .andExpect(status().isForbidden()); // User should not be allowed
    }

    @Test
    @Order(4)
    void shouldListAllGames() throws Exception {
        mockMvc.perform(get("/api/v1/catalog")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());
    }

    @Test
    @Order(5)
    void shouldListMyGames() throws Exception {
        // Register dev first
        NewDeveloperRequest newDeveloperRequest = new NewDeveloperRequest("DevName", "DevDescription");
        mockMvc.perform(post("/api/v1/catalog/developer")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newDeveloperRequest)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/catalog/my-games")
                        .header("Authorization", "Bearer " + accessToken)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());
    }

    @Test
    @Order(6)
    void shouldListPurchasedGames() throws Exception {
        mockMvc.perform(get("/api/v1/catalog/purchased-games")
                        .header("Authorization", "Bearer " + accessToken)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());
    }

    @Test
    @Order(7)
    void shouldListCustomGames() throws Exception {
        CustomGameListRequest customGameListRequest = new CustomGameListRequest(List.of(1L, 2L));

        mockMvc.perform(post("/api/v1/catalog/custom-game-list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customGameListRequest))
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());
    }
}