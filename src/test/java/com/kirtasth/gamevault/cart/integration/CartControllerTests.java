package com.kirtasth.gamevault.cart.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.kirtasth.gamevault.cart.infrastructure.dtos.requests.AddItemPetitionRequest;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CartControllerTests extends BaseIntegrationTest {

    private String accessToken;

    @BeforeEach
    void setUp() throws Exception {
        // Register user
        NewUserRequest registerRequest = new NewUserRequest();
        registerRequest.setUsername("cartuser");
        registerRequest.setEmail("cartuser@example.com");
        registerRequest.setPassword("password123");

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());

        // Login to get token
        CredentialsRequest loginRequest = new CredentialsRequest();
        loginRequest.setEmail("cartuser@example.com");
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
    void shouldGetEmptyCart() throws Exception {
        mockMvc.perform(get("/api/v1/cart")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());
    }

    @Test
    @Order(2)
    void shouldAddItemToCartAndClear() throws Exception {
        // 1. Create a game first to add it
        // 1.a Register dev
        NewDeveloperRequest newDeveloperRequest = new NewDeveloperRequest("DevName", "DevDescription");
        mockMvc.perform(post("/api/v1/catalog/developer")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newDeveloperRequest)))
                .andExpect(status().isCreated());

        // 1.b Create Game
        MockMultipartFile image = new MockMultipartFile("image", "game.png", MediaType.IMAGE_PNG_VALUE, "image".getBytes());

        mockMvc.perform(multipart("/api/v1/catalog")
                        .file(image)
                        .param("title", "Cart Test Game")
                        .param("description", "Game for cart")
                        .param("price", "29.99")
                        .param("releaseDate", "2023-12-01T10:15:30Z")
                        .with(request -> {
                            request.setMethod("POST");
                            request.addHeader("Authorization", "Bearer " + accessToken);
                            return request;
                        }))
                .andExpect(status().isCreated());

        // Let's get the game from catalog list to be safe.
        MvcResult listResult = mockMvc.perform(get("/api/v1/catalog")
                        .param("page", "0")
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andReturn();

        String listResponseString = listResult.getResponse().getContentAsString();
        JsonNode listResponseJson = objectMapper.readTree(listResponseString);
        Long gameId = listResponseJson.get("content").get(0).get("id").asLong();

        // Let's get the cart to get its ID
        MvcResult cartResult = mockMvc.perform(get("/api/v1/cart")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andReturn();
        String cartResponseString = cartResult.getResponse().getContentAsString();
        JsonNode cartResponseJson = objectMapper.readTree(cartResponseString);
        Long cartId = cartResponseJson.get("id").asLong();

        // 2. Add to cart
        AddItemPetitionRequest addRequest = new AddItemPetitionRequest(cartId, gameId, 2);
        mockMvc.perform(post("/api/v1/cart")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addRequest)))
                .andExpect(status().isOk());

        // 2.b Get cart again to find item ID
        MvcResult cartResult2 = mockMvc.perform(get("/api/v1/cart")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode cartResponseJson2 = objectMapper.readTree(cartResult2.getResponse().getContentAsString());
        Long itemId = cartResponseJson2.get("items").get(0).get("id").asLong();

        // 2.c Remove single item
        mockMvc.perform(delete("/api/v1/cart/items/" + itemId)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());

        // 3. Clear cart
        mockMvc.perform(delete("/api/v1/cart")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isNoContent());
    }
}
