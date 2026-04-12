package com.kirtasth.gamevault.checkout.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.kirtasth.gamevault.catalog.infrastructure.dtos.requests.NewDeveloperRequest;
import com.kirtasth.gamevault.checkout.infrastructure.dtos.requests.UploadGameKeysRequest;
import com.kirtasth.gamevault.common.BaseIntegrationTest;
import com.kirtasth.gamevault.users.infrastructure.dtos.requests.CredentialsRequest;
import com.kirtasth.gamevault.users.infrastructure.dtos.requests.NewUserRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class GameKeyControllerTests extends BaseIntegrationTest {

    private String devToken;
    private String otherDevToken;
    private Long gameId;

    @BeforeEach
    void setUp() throws Exception {
        // Register and login dev 1
        devToken = registerAndLogin("dev1", "dev1@example.com");
        registerDeveloper(devToken, "Dev 1", "Description 1");

        // Register and login dev 2
        otherDevToken = registerAndLogin("dev2", "dev2@example.com");
        registerDeveloper(otherDevToken, "Dev 2", "Description 2");

        // Create game for dev 1
        gameId = createGame(devToken, "Game Dev 1");
    }

    private String registerAndLogin(String username, String email) throws Exception {
        NewUserRequest registerRequest = new NewUserRequest();
        registerRequest.setUsername(username);
        registerRequest.setEmail(email);
        registerRequest.setPassword("password123");

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());

        CredentialsRequest loginRequest = new CredentialsRequest();
        loginRequest.setEmail(email);
        loginRequest.setPassword("password123");

        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String responseString = result.getResponse().getContentAsString();
        JsonNode responseJson = objectMapper.readTree(responseString);
        return responseJson.get("accessToken").asText();
    }

    private void registerDeveloper(String token, String name, String description) throws Exception {
        NewDeveloperRequest request = new NewDeveloperRequest(name, description);
        mockMvc.perform(post("/api/v1/catalog/developer")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    private Long createGame(String token, String title) throws Exception {
        MockMultipartFile image = new MockMultipartFile("image", "game.png", MediaType.IMAGE_PNG_VALUE, "image".getBytes());

        mockMvc.perform(multipart("/api/v1/catalog")
                        .file(image)
                        .param("title", title)
                        .param("description", "Description")
                        .param("price", "19.99")
                        .param("releaseDate", "2024-01-01T00:00:00Z")
                        .with(request -> {
                            request.setMethod("POST");
                            request.addHeader("Authorization", "Bearer " + token);
                            return request;
                        }))
                .andExpect(status().isCreated());

        MvcResult result = mockMvc.perform(get("/api/v1/catalog")
                        .param("title", title))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode responseJson = objectMapper.readTree(result.getResponse().getContentAsString());
        return responseJson.get("content").get(0).get("id").asLong();
    }

    @Test
    void shouldUploadAndKeys() throws Exception {
        UploadGameKeysRequest request = new UploadGameKeysRequest();
        request.setKeys(List.of("KEY-1", "KEY-2", "KEY-3"));

        mockMvc.perform(post("/api/v1/checkout/games/" + gameId + "/keys")
                        .header("Authorization", "Bearer " + devToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/checkout/games/" + gameId + "/keys")
                        .header("Authorization", "Bearer " + devToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].keyValue").value("KEY-1"));
    }

    @Test
    void shouldMarkKeyAsUsed() throws Exception {
        UploadGameKeysRequest uploadRequest = new UploadGameKeysRequest();
        uploadRequest.setKeys(List.of("KEY-TO-MARK"));

        mockMvc.perform(post("/api/v1/checkout/games/" + gameId + "/keys")
                        .header("Authorization", "Bearer " + devToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(uploadRequest)))
                .andExpect(status().isCreated());

        MvcResult result = mockMvc.perform(get("/api/v1/checkout/games/" + gameId + "/keys")
                        .header("Authorization", "Bearer " + devToken))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode keys = objectMapper.readTree(result.getResponse().getContentAsString());
        Long keyId = keys.get(0).get("id").asLong();

        mockMvc.perform(patch("/api/v1/checkout/game-keys/" + keyId + "/mark-as-used")
                        .header("Authorization", "Bearer " + devToken))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/checkout/games/" + gameId + "/keys")
                        .header("Authorization", "Bearer " + devToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].isUsed").value(true));
    }

    @Test
    void shouldForbiddenWhenUploadingToOtherDevGame() throws Exception {
        UploadGameKeysRequest request = new UploadGameKeysRequest();
        request.setKeys(List.of("STOLEN-KEY"));

        mockMvc.perform(post("/api/v1/checkout/games/" + gameId + "/keys")
                        .header("Authorization", "Bearer " + otherDevToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }
}
