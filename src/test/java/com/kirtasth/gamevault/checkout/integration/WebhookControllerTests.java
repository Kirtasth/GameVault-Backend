package com.kirtasth.gamevault.checkout.integration;

import com.kirtasth.gamevault.checkout.domain.ports.out.StripePort;
import com.kirtasth.gamevault.common.BaseIntegrationTest;
import com.stripe.model.Event;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class WebhookControllerTests extends BaseIntegrationTest {

    @MockitoBean
    private StripePort stripePort;

    @Test
    void shouldHandleStripeWebhook() throws Exception {
        String payload = "{\"type\": \"some.event\"}";
        String sigHeader = "t=123,v1=abc";

        Event mockEvent = mock(Event.class);
        when(stripePort.verifyWebhookSignature(payload, sigHeader)).thenReturn(mockEvent);
        when(mockEvent.getType()).thenReturn("some.event");

        mockMvc.perform(post("/api/v1/checkout/webhook")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload)
                        .header("Stripe-Signature", sigHeader))
                .andExpect(status().isOk());
    }
}
