package com.phaneendra.payment_orchestration.provider;

import com.phaneendra.payment_orchestration.model.Payment;
import org.springframework.stereotype.Component;

import java.util.Random;


//UPI
@Component
public class ProviderB implements PaymentProvider {

    private final Random random = new Random();

    @Override
    public String getName() {
        return "ProviderB";
    }

    @Override
    public boolean process(Payment payment) {

        // Slightly higher success rate
        return random.nextInt(100) < 70;
    }
}