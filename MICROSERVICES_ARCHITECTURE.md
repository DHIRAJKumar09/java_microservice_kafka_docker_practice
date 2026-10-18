# Microservices Architecture

A Java and Spring Boot monorepo with independently deployable services, Kong API Gateway, and Kafka for asynchronous communication. The example domain is e-commerce; rename the services to suit your project.

## Repository structure

```text
microservices-platform/
├── README.md
├── pom.xml                         # Parent Maven configuration
├── .env.example                    # Environment variable names; no secrets
├── .gitignore
├── services/
│   ├── user-service/
│   ├── product-service/
│   ├── order-service/
│   ├── inventory-service/
│   ├── payment-service/
│   └── notification-service/
├── contracts/
│   ├── rest/                       # OpenAPI specifications
│   │   ├── users.yaml
│   │   ├── products.yaml
│   │   └── orders.yaml
│   └── events/                     # Versioned Kafka event schemas
│       ├── order-created-v1.json
│       ├── inventory-reserved-v1.json
│       └── payment-completed-v1.json
├── infrastructure/
│   ├── kong/
│   │   └── kong.yml                # Routes, upstreams, plugins
│   ├── kafka/
│   │   ├── topics.yaml             # Input to the topic creation script
│   │   └── create-topics.sh
│   ├── postgres/
│   │   └── init-databases.sql       # Separate databases and users
│   ├── redis/
│   │   └── redis.conf
│   └── keycloak/
│       └── realm-export.json       # Local identity configuration
├── observability/
│   ├── prometheus/
│   │   └── prometheus.yml
│   ├── grafana/
│   │   ├── dashboards/
│   │   └── provisioning/
│   ├── otel/
│   │   └── collector-config.yaml
│   ├── tempo/
│   │   └── tempo.yaml
│   └── loki/
│       └── loki.yaml
├── deployment/
│   ├── docker/
│   │   ├── compose.yaml            # Core local environment
│   │   └── compose.observability.yaml
│   └── kubernetes/                 # Add when Kubernetes is needed
│       ├── base/
│       └── overlays/
│           ├── dev/
│           ├── staging/
│           └── production/
├── tests/
│   ├── end-to-end/
│   ├── contract/
│   └── load/
├── scripts/
│   ├── start-local.ps1
│   ├── stop-local.ps1
│   └── seed-data.ps1
├── docs/
│   ├── architecture.md
│   ├── event-flows.md
│   └── decisions/
└── .github/
    └── workflows/
        ├── ci.yml
        └── deploy.yml
```

## Structure inside each service

Apply the following structure to each Spring Boot service:

```text
order-service/
├── pom.xml
├── Dockerfile
└── src/
    ├── main/
    │   ├── java/com/example/orders/
    │   │   ├── OrderApplication.java
    │   │   ├── api/                # REST controllers and DTOs
    │   │   ├── application/        # Use cases and transactions
    │   │   ├── domain/             # Business rules and models
    │   │   ├── infrastructure/
    │   │   │   ├── persistence/    # Entities and repositories
    │   │   │   ├── messaging/      # Kafka producers and consumers
    │   │   │   └── clients/        # Calls to other services
    │   │   └── config/             # Security and configuration
    │   └── resources/
    │       ├── application.yml
    │       ├── application-local.yml
    │       └── db/migration/       # Flyway migrations
    └── test/
        ├── java/
        └── resources/
```

## Tools and responsibilities

| Tool | Purpose |
| --- | --- |
| Spring Boot | Independently deployable business services |
| Kong Gateway | Route incoming API requests and apply gateway policies |
| Kafka | Exchange asynchronous business events |
| PostgreSQL | Store data owned by each service |
| Flyway | Manage database schema migrations |
| Keycloak | User login and token issuance |
| Redis | Cache data where needed |
| Prometheus and Grafana | Collect metrics and display dashboards |
| OpenTelemetry and Tempo | Instrument services and store distributed traces |
| Loki | Centralized log storage; configure a log collector separately |
| Testcontainers | Integration tests with real infrastructure dependencies |
| Docker Compose | Run the system locally |
| Kubernetes | Optional deployment platform for later environments |

Kong can load declarative configuration from `kong.yml` in [DB-less mode](https://developer.konghq.com/gateway/db-less-mode/). Choose plugins compatible with this mode and your Kong edition.

The [OpenTelemetry Collector](https://opentelemetry.io/docs/collector/architecture/) receives and forwards telemetry to monitoring backends.

## Request and event flow

```text
Client ──HTTP──> Kong ──HTTP──> Order Service ──> Orders Database
                                  │
                           publish business event
                                  │
                                  v
                                Kafka
                                  │
                                  v
                         Inventory Service ──> Inventory Database
```

Services publish and consume Kafka events directly. Kong handles incoming HTTP traffic.

## Design rules

1. Each service owns its database and credentials. A single local PostgreSQL instance can host separate databases. Services must not query one another's database tables.
2. Keep business entities and repositories inside the service that owns them. Share documented REST and event contracts.
3. Validate access tokens and enforce authorization within services. Use Keycloak as the identity provider.
4. Make Kafka consumers idempotent so retries do not duplicate business effects.
5. Use a transactional outbox when database changes must reliably produce events. A relay publishes committed outbox records to Kafka.
6. Define retry policies and dead-letter handling for failed event processing. Handle business failures with compensating actions where needed.
7. Propagate trace context through HTTP calls and Kafka messages.
8. Keep credentials out of source control. Use environment variables locally and an appropriate secret store in deployed environments.
9. Give every service its own build, container image, health checks, and deployment lifecycle.

## Suggested implementation order

1. Build order-service and inventory-service with separate databases.
2. Add Kong routes for the HTTP APIs.
3. Connect the services with Kafka events and verify retry behavior.
4. Add Keycloak authentication and service-level authorization.
5. Add metrics, traces, and centralized logs.
6. Add the remaining services as required by the business flow.
7. Add Kubernetes deployment configuration when needed.

This document describes the proposed layout. The application source code, configuration files, and deployment scripts shown above still need to be implemented.
