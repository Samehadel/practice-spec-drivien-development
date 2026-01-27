# Implementation Plan: WhatsApp Virtual Queue (MVP)

**Branch**: `001-whatsapp-queue` | **Date**: 2026-01-27 | **Spec**: [spec.md](./spec.md)
**Input**: Feature specification from `/specs/001-whatsapp-queue/spec.md`

**Note**: This template is filled in by the `/speckit.plan` command. See `.specify/templates/commands/plan.md` for the execution workflow.

## Summary

WhatsApp-based virtual queue system allowing customers to join via WhatsApp messages and receive real-time position/ETA updates. Business gets minimal mobile admin interface to manage queue progression. Built with Java Spring Boot backend and Angular frontend.

## Technical Context

<!--
  ACTION REQUIRED: Replace the content in this section with the technical details
  for the project. The structure here is presented in advisory capacity to guide
  the iteration process.
-->

**Language/Version**: Java 21 (Spring Boot 3.x)  
**Primary Dependencies**: Spring Boot Web, Spring Data JPA, Spring Validation, Spring Security, MapStruct, Liquibase, WhatsApp Business API, Angular 17+, Ngrx, Angular Signals
**Storage**: PostgreSQL and Redis for caching
**Testing**: JUnit 5, Mockito, TestContainers (Angular testing framework)  
**Target Platform**: Linux server (backend), Mobile browsers (frontend)  
**Project Type**: Web application (backend + frontend)  
**Performance Goals**: <10s response for 95% of WhatsApp messages, <3 clicks for business actions  
**Constraints**: Mobile-first admin interface, real-time notifications, FIFO queue ordering  
**Scale/Scope**: MVP for multiple businesses, ~100 concurrent queue entries per business

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

### ✅ Compliant Areas
- **Spring Boot Baseline**: Using Spring Boot 3.x with minimal dependencies
- **Architecture Boundaries**: Clear separation of API, Application, Domain, and Infrastructure layers
- **API Contracts**: Will use OpenAPI documentation with lowerCamelCase JSON
- **Automated Tests**: JUnit 5 for backend, Angular testing for frontend
- **Safe Defaults**: application.yml configuration, no secrets in git
- **Persistence**: Liquibase for migrations, MapStruct for mapping
- **Error Handling**: Custom exceptions from common exception package

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
# Web application structure
backend/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/whatsappqueue/
│   │   │       ├── api/           # Controllers, DTOs
│   │   │       ├── application/   # Services, use cases
│   │   │       ├── domain/        # Entities, business logic
│   │   │       └── infrastructure/ # Persistence, integrations
│   │   └── resources/
│   │       ├── db/changelog/      # Liquibase migrations
│   │       └── application.yml    # Configuration
│   └── test/                      # Unit and integration tests
└── pom.xml                        # Maven configuration

frontend/
├── src/
│   ├── app/
│   │   ├── components/            # Reusable UI components
│   │   ├── pages/                 # Feature pages
│   │   ├── services/             # HTTP services
│   │   └── models/                # TypeScript interfaces
│   └── assets/
├── angular.json                  # Angular configuration
└── package.json                   # NPM dependencies

docker-compose.yml                 # Local development stack
```

**Structure Decision**: Web application with separate backend (Spring Boot) and frontend (Angular) projects. Backend follows clean architecture with explicit layer boundaries. Frontend uses Angular 17+ with standalone components, Ngrx for state management, and Signals for reactive change detection.

## Phase 1 Completion Summary

✅ **All Phase 1 deliverables completed:**
- **research.md**: Technical decisions and best practices established
- **data-model.md**: Complete entity relationships and database schema
- **contracts/api.yaml**: Full OpenAPI specification with all endpoints
- **quickstart.md**: Comprehensive setup and development guide
- **Agent Context**: Updated with new technologies (Ngrx, Signals)

✅ **Constitution Check**: All requirements satisfied
✅ **Technical Context**: All NEEDS CLARIFICATION resolved
✅ **Architecture**: Clean separation with modern stack

## Next Steps

Proceed to **Phase 2** with `/speckit.tasks` command to generate actionable implementation tasks.

## Complexity Tracking

> **Fill ONLY if Constitution Check has violations that must be justified**

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| [e.g., 4th project] | [current need] | [why 3 projects insufficient] |
| [e.g., Repository pattern] | [specific problem] | [why direct DB access insufficient] |
