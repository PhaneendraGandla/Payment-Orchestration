package com.phaneendra.payment_orchestration.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String externalId; // UUID for client

    private BigDecimal amount;

    private String currency;

    private String paymentMethod; // CARD / UPI

    @Enumerated(EnumType.STRING)
    private PaymentStatus status; // INITIATED, SUCCESS, FAILED

    private String provider; // A / B

    private String idempotencyKey;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
