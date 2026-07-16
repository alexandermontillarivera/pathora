# Pathora — Find Your Path

Full-stack academic career catalog that helps students explore university programs, compare curricula, read community reviews, and discover their ideal career path.

## Architecture

```
┌────────────────────┐        HTTPS        ┌────────────────────────┐
│      Frontend      │ ──────────────────▶ │     Render backend     │
│   Svelte 5 + Vite  │                     │ Spring Boot 4.1 / Java │
└────────────────────┘                     └───────────┬────────────┘
                                                      │ JDBC/TLS
                                           ┌──────────▼────────────┐
                                           │ Managed PostgreSQL    │
                                           └───────────────────────┘
```

The frontend calls the public Render API. The backend exposes all application endpoints below `/api` and connects to managed PostgreSQL over TLS.

## Tech Stack

### Frontend

| Technology | Version |
|---|---|
| Svelte | 5.x (runes mode) |
| Vite | 8.x |
| TypeScript | 6.x |
| @dvcol/svelte-simple-router | 2.7 |
| @tabler/icons-svelte | 3.44 |

### Backend

| Technology | Version |
|---|---|
| Spring Boot | 4.1 |
| Java | 21 |
| Spring Data JPA (Hibernate) | — |
| Spring Security + OAuth2 Resource Server | — |
| PostgreSQL | 17 |
| Flyway | — (disabled, JPA ddl-auto) |
| SpringDoc OpenAPI | 3.0 |

## Project Structure

```
proyecto-final/
├── render.yaml                    # Render backend Blueprint
├── backend/
│   ├── Dockerfile                 # Render production image
│   ├── nginx.render.conf          # Root redirect and backend proxy
│   ├── pom.xml                    # Maven config
│   ├── mvnw / mvnw.cmd            # Maven wrapper
│   ├── .env.example               # Environment variables template
│   └── src/main/java/io/pathora/catalog/
│       ├── PathoraCatalogApplication.java
│       ├── config/                # Security, OpenAPI, seed data
│       ├── entities/              # 13 JPA entities
│       ├── enums/                 # CareerStatus, StudyMode
│       ├── modules/               # Feature modules (controller + service + DTO)
│       │   ├── auth/              # Registration, login, JWT, password reset
│       │   ├── career/            # Career listing & detail
│       │   ├── school/            # School listing & detail
│       │   ├── pensum/            # Curriculum by career
│       │   ├── rating/            # Career ratings (1–5)
│       │   ├── comment/           # Rich-text comments & voting
│       │   ├── saved/             # Bookmarked careers
│       │   ├── notification/      # User notifications
│       │   ├── community/         # Global activity stats
│       │   └── user/              # Public profile & settings
│       ├── repositories/          # Spring Data repositories
│       └── shared/                # API envelope, exceptions, JWT, email, pagination
└── frontend/
    ├── Dockerfile                 # Generic production image
    ├── nginx.conf                 # SPA route fallback and asset caching
    ├── package.json
    └── src/
        ├── main.ts                # Svelte 5 mount entry
        ├── app.svelte             # Root component (router, header, footer)
        ├── app.css                # Global styles
        ├── lib/
        │   ├── router.ts          # Route definitions (12 routes)
        │   ├── types.ts           # Domain types
        │   ├── api/               # HTTP client, token management, API types
        │   ├── services/          # Auth, catalog, comments, ratings, etc.
        │   ├── stores/            # Auth & UI state (Svelte runes)
        │   ├── hooks/             # Data-fetching hooks (resource, infinite scroll)
        │   ├── utils/
        │   └── components/        # Reusable UI components (auth, cards, form, layout, etc.)
        └── modules/               # Page-level components (discover, search, career, profile, etc.)
```

## Prerequisites

- **JDK 21** (or higher)
- **Node.js 22** (or higher)
- **PostgreSQL 17** database (local or cloud, e.g. [Neon](https://neon.tech))

## Local Development

### 1. Clone & install dependencies

```powershell
# Frontend
cd frontend
npm install
```

### 2. Configure environment

Copy the example files and fill in your values:

```powershell
# Backend
copy backend\.env.example backend\.env

# Frontend
copy frontend\.env.example frontend\.env
```

### 3. Start the backend

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

The API starts at `http://localhost:4000/api`.

### 4. Start the frontend

```powershell
cd frontend
npm run dev
```

The SPA starts at `http://localhost:5173`.

## Environment Variables

### Backend (`backend/.env`)

| Variable | Required | Default | Description |
|---|---|---|---|
| `DB_URL` | Yes | — | PostgreSQL JDBC connection string |
| `DB_USERNAME` | Yes | — | Database user |
| `DB_PASSWORD` | Yes | — | Database password |
| `SERVER_PORT` | No | `4000` | Local server port |
| `PORT` | No | — | Injected by the production hosting platform |
| `JWT_SECRET` | Yes | — | Base64-encoded 32-byte HS256 key |
| `JWT_EXPIRATION_MINUTES` | No | `15` | Access token lifetime |
| `JWT_REFRESH_EXPIRATION_DAYS` | No | `30` | Refresh token lifetime |
| `RESEND_API_KEY` | No | — | [Resend](https://resend.com) API key for emails |
| `RESEND_FROM` | No | `Pathora <onboarding@resend.dev>` | Sender address |
| `CORS_ALLOWED_ORIGINS` | No | `http://localhost:5173` | Comma-separated allowed origins |
| `FRONTEND_URL` | No | `http://localhost:5173` | Frontend URL (for password reset links) |

### Frontend (`frontend/.env`)

| Variable | Required | Default | Description |
|---|---|---|---|
| `VITE_API_URL` | No | `http://localhost:4000/v1` | Backend API base URL |

In production, set `VITE_API_URL` to the full Render API URL, such as `https://pathora-60cw.onrender.com/api/v1`.

## API Overview

All endpoints are prefixed with `/api`. Authentication uses Bearer JWT tokens.

### Public endpoints (no auth required)

| Method | Path | Description |
|---|---|---|
| `POST` | `/api/v1/auth/register` | Create account |
| `POST` | `/api/v1/auth/login` | Sign in |
| `POST` | `/api/v1/auth/refresh` | Rotate refresh token |
| `POST` | `/api/v1/auth/forgot-password` | Request password reset |
| `POST` | `/api/v1/auth/reset-password` | Complete password reset |
| `GET` | `/api/v1/schools` | List schools (paginated) |
| `GET` | `/api/v1/schools/{id}` | School detail |
| `GET` | `/api/v1/careers` | List careers (filterable, paginated) |
| `GET` | `/api/v1/careers/{id}` | Career detail |
| `GET` | `/api/v1/pensums/career/{careerId}` | Active curriculum |
| `GET` | `/api/v1/careers/{id}/ratings` | Career rating summary |
| `GET` | `/api/v1/careers/{id}/comments` | Career comments |
| `GET` | `/api/v1/community/stats` | Global community stats |
| `GET` | `/api/v1/users/{id}` | Public user profile |

### Authenticated endpoints

| Method | Path | Description |
|---|---|---|
| `GET` | `/api/v1/auth/me` | Current user info |
| `POST` | `/api/v1/auth/logout` | Revoke refresh token |
| `POST` | `/api/v1/careers/{id}/ratings` | Rate a career |
| `POST` | `/api/v1/careers/{id}/comments` | Post a comment |
| `POST` | `/api/v1/comments/{id}/replies` | Reply to a comment |
| `POST` | `/api/v1/comments/{id}/vote` | Vote on a comment |
| `GET` | `/api/v1/me/saved-careers` | List saved careers |
| `POST` | `/api/v1/me/saved-careers/{careerId}` | Save a career |
| `DELETE` | `/api/v1/me/saved-careers/{careerId}` | Unsave a career |
| `GET` | `/api/v1/me/notifications` | List notifications |
| `PUT` | `/api/v1/users/me` | Update profile |

### Documentation

- **Swagger UI**: `http://localhost:4000/api/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:4000/api/v3/api-docs`
- **Health check**: `http://localhost:4000/api/actuator/health`

### Pagination & filtering

List endpoints accept query parameters:

| Param | Type | Default | Description |
|---|---|---|---|
| `page` | int | `1` | Page number (1-based) |
| `max` | int | `20` | Records per page (1–100) |
| `order` | string | `ASC` | Sort direction (`ASC` or `DESC`) |

The `/api/v1/careers` endpoint additionally accepts `name`, `schoolId`, `studyMode`, and `status` filters.

## Deploying the backend to Render

The root `render.yaml` defines the backend as a Docker web service in Virginia. Create a Blueprint from the repository and provide the database, JWT, and Resend secrets requested by Render. The service uses `/api/actuator/health` as its health check and redirects its root URL to Swagger UI.

The production environment can be imported from `backend/.env.render`. Keep this file private and never commit it.

### Local testing with Docker

```powershell
# Backend
docker build -t pathora-backend backend/
docker run -p 4000:10000 --env-file backend/.env.render -e PORT=10000 pathora-backend

# Frontend (replace the API URL with the active backend)
docker build --build-arg VITE_API_URL=https://pathora-60cw.onrender.com/api/v1 -t pathora-frontend frontend/
docker run -p 3000:80 pathora-frontend
```

## Code Formatting

```powershell
# Backend (Google Java Format via Spotless)
cd backend
.\mvnw.cmd spotless:apply
.\mvnw.cmd spotless:check

# Frontend (via TypeScript compiler)
cd frontend
npm run check
```

## Frontend Routes

| Path | Page |
|---|---|
| `/` | Discover — featured careers, schools, search |
| `/schools` | All schools (infinite scroll) |
| `/schools/:id` | School detail & programs |
| `/search` | Career search with filters |
| `/careers/:id` | Career detail (overview, pensum, community) |
| `/community` | Recent comments & activity feed |
| `/profile` | Authenticated user profile & settings |
| `/users/:id` | Public user profile |
| `/activity` | Notification inbox |
| `/about` | About Pathora |
| `/privacy` | Privacy policy |
| `/reset-password` | Password reset form |
| `*` | 404 — Not found |

## License

Private — all rights reserved.
