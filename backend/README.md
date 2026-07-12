# Careers API

Modular REST API for querying and managing university careers.

## Tech Stack

- Java 21 LTS, Spring Boot 4.1, and Maven Wrapper
- Spring Web MVC, Spring Data JPA/Hibernate, and Bean Validation
- PostgreSQL 17
- OpenAPI 3 / Swagger UI
- Actuator for health checks

## Quick Start

Prerequisites: JDK 21.

```powershell
.\mvnw.cmd spring-boot:run
```

The `CatalogDataInitializer` seeds schools, careers, curricula, and sample data automatically on startup.

- API: `http://localhost:4000/api/v1/careers`
- Swagger UI: `http://localhost:4000/api/swagger-ui.html`
- OpenAPI JSON: `http://localhost:4000/api/v3/api-docs`
- Health: `http://localhost:4000/api/actuator/health`

## Core Endpoints

```http
GET /api/v1/schools
GET /api/v1/careers
GET /api/v1/careers?schoolId=1
GET /api/v1/careers?name=sistemas
GET /api/v1/careers?schoolId=1&name=ingenieria
GET /api/v1/careers/1
GET /api/v1/careers?page=2&max=10&order=DESC
```

List endpoints accept `page` (1-based), `max` (1 to 100), and `order` (`ASC` or `DESC`). All are optional; defaults are `page=1`, `max=20`, and `order=ASC`.

## Configuration

Available variables are documented in `.env.example`. The application automatically loads an `.env` file located at the repository root or inside the `backend` directory.

## Code Formatting

```powershell
# Format all compatible files
.\mvnw.cmd spotless:apply

# Check formatting without modifying files
.\mvnw.cmd spotless:check
```

## Structure

```text
src/main/java/io/pathora/catalog
├── config/                  # cross-cutting configuration
├── entities/                # JPA entities
├── enums/                   # global enumerations
├── repositories/            # persistence access layer
├── modules/
│   ├── auth/                # registration, login, JWT, password reset
│   ├── career/              # controller, service, and DTO
│   ├── school/              # controller, service, and DTO
│   ├── pensum/              # curriculum by career
│   ├── rating/              # career ratings (1–5)
│   ├── comment/             # rich-text comments and voting
│   ├── saved/               # bookmarked careers
│   ├── notification/        # user notifications
│   ├── community/           # global activity stats
│   └── user/                # public profile and settings
└── shared/
    ├── api/                 # standard response envelope
    ├── email/               # Resend email integration
    ├── exception/           # global error handling
    ├── pagination/          # paginated request/response
    └── security/            # JWT service
```
