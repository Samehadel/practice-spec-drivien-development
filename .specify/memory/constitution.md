# practice_spec_driven_development Constitution

## Core Principles

### I. Minimal, Conventional Spring Boot Baseline
Prefer Spring Boot conventions over custom frameworks. Keep the dependency set small and only add libraries with a clear, current need.

### II. Explicit Architecture Boundaries
Separate concerns clearly:

- **API layer**: controllers and request/response DTOs
- **Application layer**: use-cases/services that orchestrate business flows
- **Domain layer**: domain models and invariants
- **Infrastructure layer**: persistence, integrations, configuration

### III. API Contracts Are Stable
HTTP APIs must be documented and versioned when breaking changes are introduced. JSON uses lowerCamelCase.

### IV. Automated Tests Are Required
Every feature includes automated tests:

- **Unit tests** for core business logic
- **Integration tests** for persistence and external boundaries when applicable

### V. Safe Defaults
No secrets committed to git. Default configuration is safe (secure-by-default) and runs locally without manual setup beyond standard tooling.

## Backend Bare-Minimum Requirements (Spring Boot)

### Tooling
- **Java**: 17+
- **Build**: Maven
- **Formatting**: consistent, automated formatting (build must fail on violations)

### Runtime & Configuration
- Use `application.yml`
- Separate profiles at minimum: `default` and `test`
- Health endpoint available via Spring Boot Actuator. When ever a critical component is added, a health check should include it.

### Dependencies (minimum set)
- `spring-boot-starter-web`
- `spring-boot-starter-validation`
- `spring-boot-starter-test`

Add only as needed:

- `spring-boot-starter-actuator` (recommended)
- `spring-boot-starter-data-jpa` + DB driver (if persistence is required)
- `spring-boot-starter-security` (if authentication/authorization is required)

### Persistence & Migrations (when a DB is used)
- Use Liquibase for schema changes
- All schema/data changes are applied via migrations, not manual SQL

### Mapping
- Use MapStruct for object mapping between entities and DTOs

### Error Handling
- Use consistent error responses
- Prefer custom exceptions from the project’s common exception package

## Development Workflow & Quality Gates

### Local Run
- `./mvnw spring-boot:run`
- `./mvnw test`

### Pull Request Gate
- Tests must pass
- No TODO/FIXME left for production behavior
- Breaking API changes require explicit versioning and a migration plan

## Governance

This constitution is the default standard for backend work in this repo. Any deviation must be documented in the PR description with:

- **Reason**
- **Scope and impact**
- **Rollback plan**

**Version**: 1.0.0 | **Ratified**: 2026-01-22 | **Last Amended**: 2026-01-22
