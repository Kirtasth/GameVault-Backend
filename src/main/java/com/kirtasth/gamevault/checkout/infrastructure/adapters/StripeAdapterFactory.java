package com.kirtasth.gamevault.checkout.infrastructure.adapters;

import com.kirtasth.gamevault.checkout.domain.ports.out.StripePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class StripeAdapterFactory {

    private final List<StripePort> stripeAdapters;
    private Map<String, StripePort> adaptersMap;

    private synchronized void initializeMap() {
        if (adaptersMap == null) {
            adaptersMap = stripeAdapters.stream()
                    .collect(Collectors.toMap(StripePort::getEventType, Function.identity()));
        }
    }

    public Optional<StripePort> getAdapter(String eventType) {
        if (adaptersMap == null) {
            initializeMap();
        }
        return Optional.ofNullable(adaptersMap.get(eventType));
    }
}
