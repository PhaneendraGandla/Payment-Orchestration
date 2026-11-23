# Payment Orchestration System

A Spring Boot based backend system that demonstrates a clean and extensible payment processing architecture with provider routing, retry mechanism, failover support, and idempotency handling.

## Overview

This project simulates a real-world payment orchestration layer. Instead of directly calling a single payment gateway, the system routes the payment request to the appropriate provider based on the payment method, applies retry logic on failure, and supports failover to another provider when needed.

## Key Features

- Payment request processing
- Provider-based routing (CARD → ProviderA, UPI → ProviderB)
- Retry mechanism for failed provider calls
- Automatic failover support
- Idempotency support using `Idempotency-Key` header
- Persistent storage of payment records
- Clean layered architecture
- Global exception handling
- Structured API responses

## Tech Stack

- **Language:** Java 17
- **Framework:** Spring Boot
- **ORM:** Spring Data JPA + Hibernate
- **Database:** MySQL
- **Build Tool:** Maven
- **Utilities:** Lombok

## API Endpoints

### 1. Create Payment

```
POST /payments
```

**Optional Header:**
```
Idempotency-Key: unique-key-123
```

**Request Body:**
```json
{
  "amount": 1500.00,
  "currency": "INR",
  "paymentMethod": "CARD"
}
```

**Sample Success Response:**
```json
{
  "paymentId": "a3f1c2d4-9b8e-4f1a-9c3d-2e7b6a1f0d9e",
  "status": "SUCCESS",
  "provider": "ProviderA"
}
```

### 2. Get Payment by ID

```
GET /payments/{id}
```

**Sample Response:**
```json
{
  "paymentId": "a3f1c2d4-9b8e-4f1a-9c3d-2e7b6a1f0d9e",
  "status": "SUCCESS",
  "provider": "ProviderA"
}
```

## Architecture & Project Structure

```
com.phaneendra.payment_orchestration
├── controller
│   └── PaymentController.java          # Exposes REST APIs
├── service
│   ├── PaymentOrchestrator.java        # Main orchestration logic
│   └── RetryService.java               # Handles retry mechanism
├── routing
│   └── RoutingService.java             # Decides which provider to use
├── provider
│   ├── PaymentProvider.java            # Provider interface
│   ├── ProviderA.java                  # CARD provider
│   └── ProviderB.java                  # UPI provider
├── repository
│   └── PaymentRepository.java          # Database access layer
├── model
│   ├── Payment.java                    # Payment entity
│   └── PaymentStatus.java              # Enum (INITIATED, SUCCESS, FAILED)
├── dto
│   ├── PaymentRequest.java
│   └── PaymentResponse.java
└── exception
    ├── GlobalExceptionHandler.java
    ├── PaymentNotFoundException.java
    └── ErrorResponse.java
```

## How the System Works

1. Client sends a payment request (optionally with Idempotency-Key).
2. System checks if a payment with the same Idempotency-Key already exists.
3. If not, a new payment record is created with status `INITIATED`.
4. `RoutingService` selects the appropriate provider based on payment method.
5. `RetryService` attempts the payment with retry logic.
6. If the primary provider fails, the system tries failover.
7. Final status (`SUCCESS` / `FAILED`) is updated and returned to the client.

## How to Run the Project

1. Clone the repository:
```bash
git clone https://github.com/PhaneendraGandla/Payment-Orchestration.git
```

2. Navigate into the project directory:
```bash
cd Payment-Orchestration
```

3. Update the database configuration in:
```
src/main/resources/application.properties
```

4. Run the application:
```bash
mvn spring-boot:run
```

## Current Status

This is a working intermediate version of the Payment Orchestration System.  
It currently supports:

- Provider routing
- Retry logic
- Basic failover
- Idempotency handling
- Payment persistence

Future enhancements can include authentication, more payment providers, webhook support, and better observability.

## Author

**Phaneendra Gandla**
