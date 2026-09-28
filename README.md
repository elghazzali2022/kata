# Carrefour Java Kata

Subscription management kata built around the mandatory MVP.

## Stack

- Java 25
- Spring Boot 4.0.1
- Spring Web
- Spring Data JPA
- H2
- JUnit 5
- VueJS 3 + Vite

## Functional scope

### MVP

- Subscribe a customer to a plan.
- Automatic renewal through a calculated next billing date.
- Change plan at any time.
- Upgrade and downgrade.
- Calculate a prorated adjustment.
- Apply the adjustment to the next billing cycle.

## Business decisions

The exercise does not provide prices or detailed acceptance criteria. The following assumptions are intentionally documented.

| Plan | Price | Duration |
|---|---:|---:|
| MONTHLY | 10.00 | 30 days |
| QUARTERLY | 27.00 | 90 days |
| ANNUAL | 96.00 | 365 days |

Prices are implementation assumptions and are centralized in `PlanProvider`.

### Plan change

When a customer changes plan:

1. The new plan becomes active immediately.
2. The unused value of the current plan is calculated.
3. The value of the new plan for the remaining period is calculated.
4. The difference is stored as a billing adjustment.
5. The adjustment is applied to the next billing cycle.

A negative adjustment represents a credit for the customer; a positive adjustment represents an additional amount.

The calculation uses `BigDecimal` and is isolated in the domain so it can be tested without Spring or a database.

## Architecture

The backend follows a pragmatic Clean Architecture:

```text
api
  -> application
      -> domain
          -> repository interface

infrastructure
  -> implements persistence and configuration
```

- `domain`: business objects and business rules. No Spring dependency.
- `application`: use cases and orchestration.
- `api`: REST controllers and DTOs.
- `infrastructure`: JPA persistence and plan configuration.

The architecture is intentionally small for a two-hour kata. No microservices, messaging, CQRS, or other patterns are introduced because they do not add value to this scope.

## API

### Create subscription

`POST /api/subscriptions`

```json
{
  "customerId": "11111111-1111-1111-1111-111111111111",
  "plan": "MONTHLY"
}
```

### Get subscription

`GET /api/subscriptions/{customerId}`

### Change plan

`PUT /api/subscriptions/{customerId}/plan`

```json
{
  "plan": "ANNUAL"
}
```

## Run backend

Requirements:

- JDK 25
- Maven 3.9+

```bash
cd backend
./mvnw test
./mvnw spring-boot:run
```

On Windows:

```powershell
cd backend
mvnw.cmd test
mvnw.cmd spring-boot:run
```

The API starts on `http://localhost:8080`.

H2 console:

`http://localhost:8080/h2-console`

JDBC URL:

`jdbc:h2:mem:carrefour`

User: `sa`

Password: empty.

## Run frontend

Requirements:

- Node.js 20+

```bash
cd frontend
npm install
npm run dev
```

The frontend starts on the Vite development URL, normally `http://localhost:5173`.

## Tests

The tests focus on business behavior:

- subscription creation
- upgrade
- downgrade
- same-plan change
- end-of-period behavior
- prorated calculation

Run:

```bash
cd backend
./mvnw test
```

## Technical choices

### BigDecimal

Money is represented with `BigDecimal` instead of floating point types.

### Clock

The application uses `java.time.Clock` so date-dependent business rules are deterministic in tests.

### H2

H2 keeps the kata self-contained. A production implementation could replace it with PostgreSQL without changing the domain model.

### DTOs

REST DTOs are kept separate from domain objects to avoid coupling the domain to the API contract.

## Limitations / possible improvements

- Authentication and authorization are outside the MVP.
- Payment processing is outside the MVP.
- Real payment provider integration is not implemented.
- Pricing is in-memory and could be moved to a dedicated pricing store.
- Automatic renewal is represented by the calculated next billing date; no scheduled payment job is implemented.
- A production billing system would require explicit treatment of taxes, currencies, payment failures, idempotency and audit history.

## Suggested Git history

The repository should be committed progressively, for example:

```text
chore: initialize Spring Boot project with Java 25
feat(domain): model subscription plans and subscriptions
feat(application): implement subscription creation
feat(domain): implement prorated billing calculation
test(domain): cover subscription proration scenarios
feat(application): implement subscription plan changes
feat(api): expose subscription REST endpoints
feat(frontend): add subscription management interface
docs: document architecture and business decisions
```
