# Implementation Plan: WhatsApp Virtual Queue

**Branch**: `001-whatsapp-queue` | **Date**: 2026-02-14 | **Spec**: [spec.md](./spec.md)
**Input**: Feature specification from `/specs/001-whatsapp-queue/spec.md`

**Note**: This template is filled in by the `/speckit.plan` command. See `.specify/templates/commands/plan.md` for the execution workflow.

## Summary

WhatsApp-based virtual queue system allowing service businesses to manage walk-in customers digitally. The system provides real-time queue visibility through WhatsApp messaging with a minimal web admin interface for business operators. Technical approach uses Spring Boot backend with Angular frontend, integrating WhatsApp API for messaging and following established exception handling patterns.

## Technical Context

<!--
  ACTION REQUIRED: Replace the content in this section with the technical details
  for the project. The structure here is presented in advisory capacity to guide
  the iteration process.
-->

**Language/Version**: Java 17+ (Spring Boot), TypeScript (Angular 17+)  
**Primary Dependencies**: Spring Boot (web, validation, data-jpa, actuator), Angular, WhatsApp API, PostgreSQL, Liquibase, MapStruct  
**Storage**: PostgreSQL with Liquibase migrations  
**Testing**: JUnit 5, Spring Boot Test, Jest, Angular Testing Utilities  
**Target Platform**: Linux server (backend), Modern web browsers (frontend)  
**Project Type**: Web application (backend + frontend)  
**Performance Goals**: <200ms response time for WhatsApp webhook processing, support 1000+ concurrent queue operations  
**Constraints**: WhatsApp API rate limits, message delivery reliability, FIFO queue ordering  
**Scale/Scope**: Multi-tenant support for multiple businesses, 10k+ concurrent users across all queues

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

### ✅ Final Compliance Check (Post-Design)

**I. Minimal Spring Boot Baseline**: ✅
- Using Spring Boot 3.x conventions with Java 21
- Dependency set limited to clear needs (web, validation, data-jpa, actuator)
- Following established patterns from exception handling design
- MapStruct for object mapping per constitutional requirements

**II. Architecture Boundaries**: ✅
- API layer: controllers and DTOs clearly defined
- Application layer: services orchestrating business flows
- Domain layer: queue management entities and invariants
- Infrastructure layer: persistence (PostgreSQL), WhatsApp integration, Redis caching

**III. API Contracts**: ✅
- HTTP APIs documented with OpenAPI 3.0.3 specification
- JSON uses lowerCamelCase per Java convention
- Versioning strategy established (v1 API path)
- Comprehensive error response schemas

**IV. Automated Tests**: ✅
- Unit tests planned for queue business logic and exception handling
- Integration tests for persistence and WhatsApp boundaries
- Frontend component tests with Jasmine/Karma
- E2E tests with Cypress for complete user workflows

**V. Safe Defaults**: ✅
- No secrets in git (WhatsApp API keys via environment variables)
- Safe default configuration with local development setup
- Docker Compose for isolated development environment
- Health checks via Spring Boot Actuator

### Design Compliance Verification

All design artifacts (research.md, data-model.md, contracts/api.yaml, quickstart.md) demonstrate adherence to constitutional requirements:

- **Exception Handling**: Integrated existing framework from backend/docs/exception-handling-design.md
- **Database Migrations**: Liquibase for schema changes with proper versioning
- **Mapping**: MapStruct for entity-DTO transformations
- **Testing Strategy**: Comprehensive test coverage across all layers
- **API Design**: RESTful principles with proper HTTP status codes
- **Security**: Proper authentication, authorization, and data protection

## Project Structure

### Documentation (this feature)

```text
specs/[###-feature]/
├── plan.md              # This file (/speckit.plan command output)
├── research.md          # Phase 0 output (/speckit.plan command)
├── data-model.md        # Phase 1 output (/speckit.plan command)
├── quickstart.md        # Phase 1 output (/speckit.plan command)
├── contracts/           # Phase 1 output (/speckit.plan command)
└── tasks.md             # Phase 2 output (/speckit.tasks command - NOT created by /speckit.plan)
```

### Source Code (repository root)
<!--
  ACTION REQUIRED: Replace the placeholder tree below with the concrete layout
  for this feature. Delete unused options and expand the chosen structure with
  real paths (e.g., apps/admin, packages/something). The delivered plan must
  not include Option labels.
-->

```text
# Option 2: Web application (backend + frontend)
backend/
├── src/
│   ├── main/
│   │   ├── java/com/example/whatsapp/
│   │   │   ├── api/           # Controllers and DTOs
│   │   │   ├── application/    # Services and use cases
│   │   │   ├── domain/        # Entities and business logic
│   │   │   └── infrastructure/ # Persistence and integrations
│   │   └── resources/
│   │       ├── db/changelog/   # Liquibase migrations
│   │       └── application.yml
│   └── test/
└── pom.xml

frontend/
├── src/
│   ├── app/
│   │   ├── components/    # Reusable UI components
│   │   ├── pages/         # Feature pages
│   │   ├── services/      # HTTP services
│   │   └── models/        # Frontend data models
│   └── assets/
├── angular.json
└── package.json
```

**Structure Decision**: Web application with clear backend/frontend separation. Backend follows Spring Boot conventions with layered architecture (API → Application → Domain → Infrastructure). Frontend uses Angular with component-based architecture. This aligns with existing project structure and constitutional requirements.

## Complexity Tracking

> **Fill ONLY if Constitution Check has violations that must be justified**

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|

