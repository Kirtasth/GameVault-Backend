package com.kirtasth.gamevault.checkout.integration;

import com.kirtasth.gamevault.cart.infrastructure.dtos.requests.AddItemPetitionRequest;
import com.kirtasth.gamevault.catalog.infrastructure.dtos.requests.NewDeveloperRequest;
import com.kirtasth.gamevault.checkout.domain.ports.out.StripePort;
import com.kirtasth.gamevault.common.BaseIntegrationTest;
import com.kirtasth.gamevault.users.infrastructure.dtos.requests.CredentialsRequest;
import com.kirtasth.gamevault.users.infrastructure.dtos.requests.NewUserRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MvcResult;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CheckoutControllerTests extends BaseIntegrationTest {

    @MockitoBean
    private StripePort stripePort;

    private String userToken;
    private Long gameId;

    @BeforeEach
    void setUp() throws Exception {
        String devToken = registerAndLogin("dev", "dev@checkout.com");
        registerDeveloper(devToken, "Dev", "Desc");
        gameId = createGame(devToken, "Test Game for Checkout");

        userToken = registerAndLogin("user", "user@checkout.com");
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

        return objectMapper.readTree(result.getResponse().getContentAsString()).get("accessToken").asText();
    }

    private void registerDeveloper(String token, String name, String description) throws Exception {
        mockMvc.perform(post("/api/v1/catalog/developer")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new NewDeveloperRequest(name, description))))
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
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isCreated());

        MvcResult result = mockMvc.perform(get("/api/v1/catalog").param("title", title))
                .andExpect(status().isOk())
                .andReturn();

        return objectMapper.readTree(result.getResponse().getContentAsString()).get("content").get(0).get("id").asLong();
    }

    @Test
    void shouldCreateCheckoutSession() throws Exception {
        // Get cart to get its ID
        MvcResult cartResult = mockMvc.perform(get("/api/v1/cart")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andReturn();
        Long cartId = objectMapper.readTree(cartResult.getResponse().getContentAsString()).get("id").asLong();

        // Add item to cart
        AddItemPetitionRequest addItemRequest = new AddItemPetitionRequest(cartId, gameId, 1);

        mockMvc.perform(post("/api/v1/cart")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addItemRequest)))
                .andExpect(status().isOk());

        // Mock StripePort
        when(stripePort.createCheckoutSession(any(), any())).thenReturn("https://checkout.stripe.com/test");

        // Test POST /api/v1/checkout
        mockMvc.perform(post("/api/v1/checkout")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.url").value("https://checkout.stripe.com/test"));
    }

    @Test
    void shouldFailWhenCartIsEmpty() throws Exception {
        mockMvc.perform(post("/api/v1/checkout")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isBadRequest()); // CheckoutUseCase throws EmptyCartException which is usually 400
    }
}
