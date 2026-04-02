# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Repository Overview

This is a **public GitHub sample repository** (`skax-sample-develop`) demonstrating a microservices architecture (MSA) for an SCM (Supply Chain Management) system. The project is organized as a polyrepo structure with four main domains: Frontend, Backend, AI Agent, and DevOps.

**Repository URL:** `https://github.com/skcc-wooyoungmoon/skax-sample-develop`

## Architecture

This project follows a **4-tier MSA architecture**:

1. **Frontend** (`skax-frontend/`) - Next.js applications
   - `admin-web` - Admin/analyst web application
   - `user-web` - End-user web application

2. **Backend** (`skax-backend/`) - Spring Boot microservices
   - `gateway/api-gateway` - API Gateway (JWT-based authentication/authorization)
   - `common/common-service` - Common services (user management, system settings)
   - `common/account-service` - User account management
   - `business/` - Domain services (demand, resource, operation, performance management)

3. **AI Agent** (`skax-agent/`) - AI/ML components and MCP servers

4. **DevOps** (`skax-devops/`) - CI/CD, Helm charts, observability

**Key architectural principles:**
- Each service is designed as an independent repository unit
- GitOps-based deployment using GitHub Actions + GHCR (GitHub Container Registry)
- Docker Compose for local development
- Services communicate through API Gateway for security and routing

## Common Development Commands

### Local Development (Docker Compose)

```bash
# Start all services (DB, Redis, Backend, Frontend)
docker compose up -d

# Start specific services
docker compose up -d postgres redis

# Rebuild and restart after code changes
docker compose up -d --build

# Rebuild specific service
docker compose up -d --build common-service

# Stop all services
docker compose down
```

**Default ports:**
- Frontend (scm-admin-web): `3000`
- API Gateway: `8081`
- Common Service: `8082`
- PostgreSQL: `5432`
- Redis: `6379`

### Frontend Development (Next.js)

Navigate to frontend project directory first: `cd skax-frontend/admin-web`

```bash
# Install dependencies
npm install

# Development server
npm run dev

# Type checking
npm run type-check

# Linting
npm run lint

# Production build (static export to out/)
npm run build
```

**Environment setup:**
- Copy `.env.example` to `.env.local`
- Set `NEXT_PUBLIC_API_BASE_URL` (default: `http://localhost:8081` for gateway, or `http://localhost:8082` for direct backend)

**Tech stack:**
- Framework: Next.js 15.2+ (App Router)
- React: 19.0+
- TypeScript: 5.8+
- HTTP Client: Axios 1.9+

### Backend Development (Spring Boot)

Navigate to backend service directory first (e.g., `cd skax-backend/common/common-service`)

```bash
# Build project
./gradlew clean build

# Run application (default profile)
./gradlew bootRun

# Run with specific profile (local profile auto-applies schema.sql)
./gradlew bootRun --args="--spring.profiles.active=local"

# Run tests
./gradlew clean test

# Health check
curl http://localhost:8082/actuator/health
```

**On Windows, use `gradlew.bat` instead of `./gradlew`**

**Tech stack:**
- Java: 21 (toolchain)
- Spring Boot: 3.5.12
- Gradle: 9.4.0
- Database: PostgreSQL 17
- Cache: Redis 8
- ORM: MyBatis 3.0.4
- Security: Spring Security + JWT (jjwt 0.11.5)

**Database setup:**
- Option A: Manually run `src/main/resources/schema.sql` via DBeaver/psql
- Option B: Use `--spring.profiles.active=local` to auto-apply schema on startup
- Connection: `localhost:5432`, DB: `scm`, User: `postgres`, Password: `changeit`

### Running a Single Test

**Frontend:**
```bash
cd skax-frontend/admin-web
# Run specific test file (if Jest/Vitest is configured)
npm test -- <test-file-pattern>
```

**Backend:**
```bash
cd skax-backend/common/common-service
# Run specific test class
./gradlew test --tests com.example.YourTestClass

# Run tests matching pattern
./gradlew test --tests '*User*'
```

## CI/CD Workflow

This project uses **GitHub Actions** with a GitOps approach:

1. **determine-environment** - Decides dev/prod based on branch
   - `main` → production environment (`prd-env`)
   - `develop`, `feature/*`, `hotfix/*` → development environment (`dev-env`)

2. **build** - Builds Docker image and pushes to GHCR
   - Image tag format: `<env>-<yyyymmdd>.<run-number>-<short-sha>`
   - Registry: `ghcr.io/<owner>/<app-name>`

3. **deploy** - Updates Helm values with new image tag via GitOps
   - Commits image tag to `skax-devops/scm-cicd/helm-charts/*/values-<env>.yaml`
   - Supports optional deploy hooks (Vercel, Cloudflare Pages, Render, Fly.io)

**Workflow files:** `.github/workflows/`
- `frontend-admin-web.yaml`
- `backend-api-gateway.yaml`
- `backend-common-common.yaml`

**Required GitHub settings:**
- Environments: `dev-env`, `prd-env`
- Workflow permissions: Read and write (for GHCR push)
- GITHUB_TOKEN automatically used for GHCR authentication

**Workflow triggers:**
- Push to branches: `main`, `develop`, `feature/*`, `hotfix/*`
- Path filters to avoid unnecessary builds (e.g., `!**/*.md`)
- Manual workflow_dispatch with environment selection

## Project Structure

### Frontend Structure (Next.js App Router)

```
skax-frontend/admin-web/
├── src/
│   ├── app/              # Next.js routes (pages, layouts)
│   ├── components/       # Reusable UI components (domain-grouped)
│   ├── services/         # API layer (auth, http client, domain services)
│   │   ├── auth/         # authService.ts, tokenStorage.ts
│   │   ├── http/         # client.ts (axios instance + interceptors)
│   │   └── sample/       # sampleService.ts
│   └── types/            # TypeScript type definitions
│       ├── common/       # api.ts (ApiResponse<T>)
│       └── sample.ts
├── Dockerfile
├── nginx.conf
├── package.json
└── tsconfig.json
```

**Key principles:**
- Pages (`app/`) should only compose components, not contain business logic
- All API calls go through `services/` layer, never direct axios in components
- Use `"use client"` directive for components with useState/useEffect/browser APIs
- CSS Modules for component-scoped styles

### Backend Structure (Spring Boot)

```
skax-backend/common/common-service/
├── src/main/java/scm/common/
│   ├── app/               # Application-level concerns
│   │   ├── config/        # Spring configurations
│   │   ├── filter/        # JWT, TraceId filters
│   │   ├── aop/           # Logging AOP
│   │   ├── auth/          # JWT allowlist management
│   │   ├── cache/         # Redis cache utilities
│   │   ├── context/       # Redis-based context storage
│   │   ├── dto/           # ApiResponse<T> wrapper
│   │   ├── exception/     # Global exception handler, ErrorCode
│   │   └── http/          # WebClient wrapper for external APIs
│   └── biz/               # Business domain packages
│       ├── user/          # User management (signup, authenticate)
│       ├── file/          # File upload/download
│       ├── sample/        # MyBatis sample CRUD
│       └── export/        # CSV/Excel export utilities
├── src/main/resources/
│   ├── application.yml
│   ├── schema.sql         # DB initialization
│   └── messages*.properties
└── build.gradle
```

**Key features:**
- TraceId filter + AOP-based request/response logging
- JWT authentication with Redis allowlist
- Global exception handling with standardized ApiResponse format
- MyBatis for database operations (PostgreSQL)
- Redis for caching and context storage

## Important Coding Standards

### Frontend

**File naming:**
- Components: PascalCase (e.g., `UserTable.tsx`)
- Services: camelCase + Service suffix (e.g., `userService.ts`)
- Hooks: `use` prefix + camelCase (e.g., `useAuth.ts`)
- Utils: kebab-case (e.g., `date-helpers.ts`)

**Function naming:**
- Start with verbs: `getDisplayName`, `handleClick`, `fetchUserList`
- Event handlers: `handle` prefix (e.g., `handleSubmit`)
- Boolean variables: `is`, `has`, `can` prefix (e.g., `isLoading`, `hasError`)

**Import order:**
1. External libraries (react, axios)
2. Internal absolute paths (@/components, @/services)
3. Relative paths (./...)
4. Styles/assets

**Before PR:**
```bash
npm run lint
npm run type-check
npm run build
```

### Backend

**Encoding:**
- All Java files: UTF-8 (enforced in build.gradle)
- JVM args for runtime: `-Dfile.encoding=UTF-8 -Dsun.jnu.encoding=UTF-8`

**Package structure:**
- `app/` - Framework/infrastructure concerns (config, filters, AOP, etc.)
- `biz/` - Business domain logic (controllers, services, repositories)

**Testing:**
- Use JUnit Platform
- `@SpringBootTest` for integration tests
- `@MyBatisTest` for repository layer tests

**Before commit:**
```bash
./gradlew clean test
./gradlew build
```

## Git Workflow

**Commit message format:**
```
<type>(<scope>): <message>

feat(admin): Add user list filter feature
fix(auth): Fix token expiry handling
docs(readme): Update frontend development guide
```

**Types:** `feat`, `fix`, `docs`, `refactor`, `test`, `chore`

**Branch strategy:**
- `main` - Production releases
- `develop` - Development integration
- `feature/*` - New features
- `hotfix/*` - Production fixes

## Authentication Flow

1. **Signup:** POST `/api/v1/common/users/signup` (email, password, username)
2. **Login:** POST `/api/v1/common/users/authenticate` (email, password)
   - Returns JWT `accessToken`
   - Token stored in localStorage (`tokenStorage.ts`)
   - Token added to Redis allowlist
3. **Authenticated requests:**
   - Frontend adds `Authorization: Bearer <token>` via axios interceptor
   - Gateway validates JWT and checks Redis allowlist
   - On 401, frontend clears token and redirects to login

## Database

**PostgreSQL schema:**
- `users` table - User accounts (id, email, password_hash, username, roles)
- `sample_mybatis_item` table - Sample CRUD demo (id, name, description, created_at, updated_at)

**Redis usage:**
- JWT allowlist: `jwt:allowlist:<username>` → token
- Cache: Various application caches via Spring Cache abstraction
- Context storage: Key-value pairs for cross-request context

## Common Issues

1. **Gradle permission denied:**
   ```bash
   chmod +x gradlew
   git update-index --chmod=+x gradlew
   ```

2. **GHCR push denied:**
   - Check repository Settings → Actions → Workflow permissions
   - Must be "Read and write permissions"

3. **Frontend API connection fails:**
   - Verify `.env.local` has correct `NEXT_PUBLIC_API_BASE_URL`
   - Check backend is running on expected port
   - Verify CORS settings in backend allow frontend origin

4. **Korean characters broken on Windows:**
   - Ensure `gradle.properties` has UTF-8 settings
   - JVM args include `-Dfile.encoding=UTF-8`

## Key Documentation Files

- `README.md` - Repository overview
- `docs/100-msa.md` - MSA architecture details
- `docs/110-repo.md` - Repository structure
- `docs/10.ci-cd-workflow-guide.md` - CI/CD setup guide
- `docs/00.OpenSource&Platform.md` - Open source and platform information
- `skax-frontend/admin-web/README.md` - Frontend development guide
- `skax-backend/common/common-service/README.md` - Backend development guide

## External Dependencies

**Maven repository:**
- Custom Nexus: `https://nexus.skax.co.kr/repository/maven-public/` (referenced in build.gradle)

**External systems (referenced in architecture):**
- OKTA - Authentication provider
- SK AX - ERP integrations (purchase info, PROMISE, myHR)
- GitHub - VCS and CI/CD
- NPO Platform - AI agent management platform

## Directory Naming Convention

**Important:** This repository has undergone directory reorganization to use simplified names:

**Old structure (deprecated):**
- `skax-scm-frontend/` → Now: `skax-frontend/`
- `skax-scm-backend/` → Now: `skax-backend/`
- `skax-scm-agent/` → Now: `skax-agent/`
- `skax-scm-devops/` → Now: `skax-devops/`
- `skax-scm-frontend/scm-admin-web/` → Now: `skax-frontend/admin-web/`
- `skax-scm-frontend/scm-user-web/` → Now: `skax-frontend/user-web/`
- `skax-scm-backend/gateway/scm-api-gateway/` → Now: `skax-backend/gateway/api-gateway/`
- `skax-scm-backend/common/scm-common-service/` → Now: `skax-backend/common/common-service/`
- `skax-scm-backend/common/scm-account-service/` → Now: `skax-backend/common/account-service/`

**Current structure:**
- Top-level: `skax-frontend/`, `skax-backend/`, `skax-agent/`, `skax-devops/`
- All subdirectories use simplified names without `scm-` prefix
- Example paths:
  - Frontend: `skax-frontend/admin-web/`, `skax-frontend/user-web/`
  - Backend: `skax-backend/gateway/api-gateway/`, `skax-backend/common/common-service/`
  - DevOps: `skax-devops/scm-cicd/helm-charts/`
  - Agent: `skax-agent/`

When referencing paths in documentation or code, always use the current simplified structure.

## When Making Changes

1. **Always read relevant README files** in the subdirectory you're modifying
2. **Frontend changes:** Test with `npm run dev`, then run lint/type-check/build before commit
3. **Backend changes:** Ensure tests pass with `./gradlew test`, verify UTF-8 encoding for Korean text
4. **API changes:** Update both frontend service layer and backend controller
5. **Schema changes:** Update `schema.sql` and document in relevant README
6. **CI/CD changes:** Test workflow with workflow_dispatch before merging to main
7. **Follow naming conventions** strictly - this codebase has detailed standards in READMEs
8. **Path references:** Use current simplified directory structure (e.g., `common-service` not `scm-common-service`)
