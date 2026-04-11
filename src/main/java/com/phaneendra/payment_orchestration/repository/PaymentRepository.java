package com.phaneendra.payment_orchestration.repository;


import com.phaneendra.payment_orchestration.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByExternalId(String externalId);

    Optional<Payment> findByIdempotencyKey(String key);
}