package com.kirtasth.gamevault.checkout.infrastructure.adapters;

import com.kirtasth.gamevault.checkout.domain.ports.out.PaymentEventHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PaymentWebhookAdapterFactory {

    private final List<PaymentEventHandler> eventHandlers;
    private Map<String, PaymentEventHandler> handlersMap;

    private synchronized void initializeMap() {
        if (handlersMap == null) {
            handlersMap = eventHandlers.stream()
                    .collect(Collectors.toMap(PaymentEventHandler::getEventType, Function.identity()));
        }
    }

    public Optional<PaymentEventHandler> getHandler(String eventType) {
        if (handlersMap == null) {
            initializeMap();
        }
        return Optional.ofNullable(handlersMap.get(eventType));
    }
}
