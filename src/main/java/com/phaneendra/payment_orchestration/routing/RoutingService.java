package com.phaneendra.payment_orchestration.routing;

import com.phaneendra.payment_orchestration.provider.PaymentProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoutingService {

    private final List<PaymentProvider> providers;

    public PaymentProvider route(String paymentMethod) {

        return providers.stream()
                .filter(provider -> {
                    if ("CARD".equalsIgnoreCase(paymentMethod) && provider.getName().equals("ProviderA")) {
                        return true;
                    }
                    if ("UPI".equalsIgnoreCase(paymentMethod) && provider.getName().equals("ProviderB")) {
                        return true;
                    }
                    return false;
                })
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No provider found"));
    }
}