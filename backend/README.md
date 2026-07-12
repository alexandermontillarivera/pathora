# Careers API

API REST modular para consultar y administrar careers universitarias.

## Tecnologías

- Java 25 LTS, Spring Boot 4.1 y Maven Wrapper
- Spring Web MVC, Spring Data JPA/Hibernate y Bean Validation
- PostgreSQL 17 y migraciones Flyway
- OpenAPI 3 / Swagger UI
- Actuator para health checks

## Inicio rápido

Requisitos: JDK 25.

```powershell
.\mvnw.cmd spring-boot:run
```

La migración crea las tablas y algunos datos de ejemplo automáticamente.

- API: `http://localhost:8080/api/v1/careers`
- Swagger UI: `http://localhost:8080/api/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/api/v3/api-docs`
- Salud: `http://localhost:8080/api/actuator/health`

## Consultas principales

```http
GET /api/v1/schools
GET /api/v1/careers
GET /api/v1/careers?schoolId=1
GET /api/v1/careers?name=sistemas
GET /api/v1/careers?schoolId=1&name=ingenieria
GET /api/v1/careers/1
GET /api/v1/careers?page=2&max=10&order=DESC
```

Los endpoints de listado aceptan `page` (desde 1), `max` (1 a 100) y `order` (`ASC` o `DESC`). Todos son opcionales; sus valores predeterminados son `page=1`, `max=20` y `order=ASC`.

El botón “Actualizar” del cliente solo necesita repetir el `GET`: cada consulta se sirve directamente desde PostgreSQL.

## Configuración

Las variables disponibles están documentadas en `.env.example`. La aplicación carga automáticamente un archivo `.env` ubicado en la raíz del repositorio o dentro de `backend`.

## Formato de código

```powershell
# Formatea todos los archivos compatibles
.\mvnw.cmd spotless:apply

# Comprueba el formato sin modificar archivos
.\mvnw.cmd spotless:check
```

## Estructura

```text
src/main/java/io/pathora/catalog
├── config/                  # configuración transversal
├── entities/                # entidades JPA globales
├── enums/                   # enumeraciones globales
├── repositories/            # acceso global a persistencia
├── modules/
│   ├── career/              # controller, service y DTO
│   └── school/              # controller, service y DTO
└── shared/
    ├── api/                 # contrato común de respuestas
    └── exception/           # manejo global de errores
```
