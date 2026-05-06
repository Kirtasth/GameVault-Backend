package com.kirtasth.gamevault.checkout.integration;

import com.kirtasth.gamevault.checkout.domain.ports.in.CheckoutUseCase;
import com.kirtasth.gamevault.common.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class WebhookControllerTests extends BaseIntegrationTest {

    @MockitoBean
    private CheckoutUseCase checkoutUseCase;

    @Test
    void shouldHandleStripeWebhook() throws Exception {
        String payload = "{\"type\": \"some.event\"}";
        String sigHeader = "t=123,v1=abc";

        doNothing().when(checkoutUseCase).handlePaymentWebhook(anyString(), anyMap());

        mockMvc.perform(post("/api/v1/checkout/webhook")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload)
                        .header("Stripe-Signature", sigHeader))
                .andExpect(status().isOk());
    }
}
