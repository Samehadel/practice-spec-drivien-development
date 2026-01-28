---

description: "Task list for WhatsApp Virtual Queue MVP implementation"
---

# Tasks: WhatsApp Virtual Queue (MVP)

**Input**: Design documents from `/specs/001-whatsapp-queue/`
**Prerequisites**: plan.md, spec.md, research.md, data-model.md, contracts/api.yaml

**Tests**: Tests are included as this is a production system requiring automated validation

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3, US4)
- Include exact file paths in descriptions

## Path Conventions

- **Backend**: `backend/src/main/java/`, `backend/src/main/resources/`, `backend/src/test/`
- **Frontend**: `frontend/src/app/`, `frontend/src/assets/`
- **Infrastructure**: Repository root (docker-compose.yml, etc.)

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and basic structure

- [x] T001 Create project structure per implementation plan
- [x] T002 Initialize Spring Boot project with Maven dependencies in backend/pom.xml
- [x] T003 Initialize Angular 17+ project with standalone components in frontend/
- [x] T004 [P] Configure backend linting with Checkstyle in backend/
- [x] T005 [P] Configure frontend linting with ESLint and Prettier in frontend/
- [x] T006 Setup Docker Compose for local development in docker-compose.yml
- [x] T007 [P] Configure Git pre-commit hooks for code formatting

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [x] T008 Setup PostgreSQL database with Liquibase in backend/src/main/resources/db/changelog/
- [x] T009 [P] Configure Redis connection and caching layer in backend/src/main/resources/application.yml
- [x] T010 [P] Setup Spring Security with JWT authentication framework in backend/src/main/java/com/example/whatsappqueue/infrastructure/security/
- [x] T011 [P] Setup API routing and controller structure in backend/src/main/java/com/example/whatsappqueue/api/
- [x] T012 Create base exception handling in backend/src/main/java/com/example/whatsappqueue/common/exception/
- [x] T013 Configure error handling and logging infrastructure in backend/src/main/resources/
- [x] T014 Setup environment configuration management in backend/src/main/resources/
- [x] T015 [P] Create MapStruct configuration in backend/src/main/java/com/example/whatsappqueue/infrastructure/mapper/
- [x] T016 Setup Angular Ngrx store structure in frontend/src/app/store/
- [x] T017 [P] Setup Angular Material UI components in frontend/src/app/
- [x] T018 Configure HTTP client services in frontend/src/app/services/api/

**Checkpoint**: Foundation ready - user story implementation can now begin in parallel

---

## Phase 3: User Story 1 - Join the queue via WhatsApp and see status (Priority: P1) 🎯 MVP

**Goal**: Customers can join a business queue by sending a WhatsApp message and receive position/ETA confirmation

**Independent Test**: Send join message to business WhatsApp number → receive confirmation with position and ETA within 10 seconds

### Tests for User Story 1 ⚠️

> **NOTE: Write these tests FIRST, ensure they FAIL before implementation**

- [ ] T019 [P] [US1] Unit test for Business entity in backend/src/test/java/com/example/whatsappqueue/domain/BusinessTest.java
- [ ] T020 [P] [US1] Unit test for QueueEntry entity in backend/src/test/java/com/example/whatsappqueue/domain/QueueEntryTest.java
- [ ] T021 [P] [US1] Integration test for WhatsApp webhook in backend/src/test/java/com/example/whatsappqueue/api/WhatsAppWebhookControllerTest.java
- [ ] T022 [P] [US1] Contract test for join queue endpoint in backend/src/test/java/com/example/whatsappqueue/api/QueueControllerTest.java

### Implementation for User Story 1

- [ ] T023 [P] [US1] Create Business entity in backend/src/main/java/com/example/whatsappqueue/domain/Business.java
- [ ] T024 [P] [US1] Create QueueEntry entity in backend/src/main/java/com/example/whatsappqueue/domain/QueueEntry.java
- [ ] T025 [P] [US1] Create Notification entity in backend/src/main/java/com/example/whatsappqueue/domain/Notification.java
- [ ] T026 [P] [US1] Create QueueStateChange entity in backend/src/main/java/com/example/whatsappqueue/domain/QueueStateChange.java
- [ ] T027 [P] [US1] Create Business repository in backend/src/main/java/com/example/whatsappqueue/infrastructure/persistence/BusinessRepository.java
- [ ] T028 [P] [US1] Create QueueEntry repository in backend/src/main/java/com/example/whatsappqueue/infrastructure/persistence/QueueEntryRepository.java
- [ ] T029 [P] [US1] Create Notification repository in backend/src/main/java/com/example/whatsappqueue/infrastructure/persistence/NotificationRepository.java
- [ ] T030 [P] [US1] Create MapStruct mappers in backend/src/main/java/com/example/whatsappqueue/infrastructure/mapper/
- [ ] T031 [US1] Implement QueueService in backend/src/main/java/com/example/whatsappqueue/application/QueueService.java (depends on T023-T030)
- [ ] T032 [US1] Implement WhatsAppService in backend/src/main/java/com/example/whatsappqueue/application/WhatsAppService.java
- [ ] T033 [US1] Implement WhatsApp webhook controller in backend/src/main/java/com/example/whatsappqueue/api/WhatsAppWebhookController.java
- [ ] T034 [US1] Implement Queue controller in backend/src/main/java/com/example/whatsappqueue/api/QueueController.java
- [ ] T035 [US1] Create Liquibase migration for Business table in backend/src/main/resources/db/changelog/versions/DDL/001-create-businesses-table.sql
- [ ] T036 [US1] Create Liquibase migration for QueueEntry table in backend/src/main/resources/db/changelog/versions/DDL/002-create-queue-entries-table.sql
- [ ] T037 [US1] Create Liquibase migration for Notification table in backend/src/main/resources/db/changelog/versions/DDL/003-create-notifications-table.sql
- [ ] T038 [US1] Create Liquibase migration for QueueStateChange table in backend/src/main/resources/db/changelog/versions/DDL/004-create-queue-state-changes-table.sql
- [ ] T039 [US1] Add validation and error handling for queue operations
- [ ] T040 [US1] Add logging for user story 1 operations
- [ ] T041 [US1] Configure Redis queue operations in backend/src/main/java/com/example/whatsappqueue/infrastructure/cache/

**Checkpoint**: At this point, User Story 1 should be fully functional and testable independently

---

## Phase 4: User Story 2 - Get proactive turn updates and notifications (Priority: P2)

**Goal**: Customers automatically receive updates when queue advances, including "nearly your turn" and "you are next" messages

**Independent Test**: Create queue with 3 customers → advance queue → verify all affected customers receive position updates and "nearly your turn" notifications

### Tests for User Story 2 ⚠️

- [ ] T042 [P] [US2] Unit test for notification service in backend/src/test/java/com/example/whatsappqueue/application/NotificationServiceTest.java
- [ ] T043 [P] [US2] Integration test for queue advancement notifications in backend/src/test/java/com/example/whatsappqueue/application/QueueServiceTest.java

### Implementation for User Story 2

- [ ] T044 [P] [US2] Create NotificationService in backend/src/main/java/com/example/whatsappqueue/application/NotificationService.java
- [ ] T045 [US2] Implement queue advancement logic in QueueService (extends T031)
- [ ] T046 [US2] Implement notification triggers for position updates in WhatsAppService (extends T032)
- [ ] T047 [US2] Add "nearly your turn" notification logic in NotificationService
- [ ] T048 [US2] Add "you are next" notification logic in NotificationService
- [ ] T049 [US2] Implement rate limiting for notifications in NotificationService
- [ ] T050 [US2] Add notification status tracking and retry logic
- [ ] T051 [US2] Add logging for user story 2 operations

**Checkpoint**: At this point, User Stories 1 AND 2 should both work independently

---

## Phase 5: User Story 3 - Leave the queue via WhatsApp (Priority: P3)

**Goal**: Customers can leave the queue by sending a WhatsApp message and receive confirmation

**Independent Test**: Join queue → send leave message → verify removal and confirmation, verify remaining customers' positions update

### Tests for User Story 3 ⚠️

- [ ] T052 [P] [US3] Unit test for leave queue functionality in backend/src/test/java/com/example/whatsappqueue/application/QueueServiceTest.java
- [ ] T053 [P] [US3] Integration test for leave queue webhook in backend/src/test/java/com/example/whatsappqueue/api/WhatsAppWebhookControllerTest.java

### Implementation for User Story 3

- [ ] T054 [P] [US3] Implement leave queue logic in QueueService (extends T031)
- [ ] T055 [US3] Add leave queue message handling in WhatsAppWebhookController (extends T033)
- [ ] T056 [US3] Implement position recalculation after customer leaves in QueueService
- [ ] T057 [US3] Add leave confirmation notification in NotificationService (extends T044)
- [ ] T058 [US3] Add validation for leave requests (customer must be in queue)
- [ ] T059 [US3] Add logging for user story 3 operations

**Checkpoint**: All user stories should now be independently functional

---

## Phase 6: User Story 4 - Business manages the queue from minimal admin view (Priority: P4)

**Goal**: Business operators can view queue and perform actions (serve, skip, mark served/no-show, open/close queue)

**Independent Test**: Create queue via customer messages → use admin interface to advance/skip/close → verify queue state changes

### Frontend Tests for User Story 4 ⚠️

- [ ] T060 [P] [US4] Unit test for queue management component in frontend/src/app/components/queue-management/queue-management.component.spec.ts
- [ ] T061 [P] [US4] Integration test for admin API endpoints in backend/src/test/java/com/example/whatsappqueue/api/BusinessControllerTest.java

### Backend Implementation for User Story 4

- [ ] T062 [P] [US4] Create Business controller in backend/src/main/java/com/example/whatsappqueue/api/BusinessController.java
- [ ] T063 [P] [US4] Implement advance queue endpoint in BusinessController
- [ ] T064 [P] [US4] Implement skip customer endpoint in BusinessController
- [ ] T065 [P] [US4] Implement mark served endpoint in BusinessController
- [ ] T066 [P] [US4] Implement mark no-show endpoint in BusinessController
- [ ] T067 [P] [US4] Implement open/close queue endpoint in BusinessController
- [ ] T068 [P] [US4] Create BusinessService in backend/src/main/java/com/example/whatsappqueue/application/BusinessService.java
- [ ] T069 [US4] Add queue state validation and error handling in BusinessService
- [ ] T070 [US4] Add audit logging for business operations in BusinessService

### Frontend Implementation for User Story 4

- [ ] T071 [P] [US4] Create queue management component in frontend/src/app/components/queue-management/queue-management.component.ts
- [ ] T072 [P] [US4] Create queue display component in frontend/src/app/components/queue-display/queue-display.component.ts
- [ ] T073 [P] [US4] Create business settings component in frontend/src/app/components/business-settings/business-settings.component.ts
- [ ] T074 [P] [US4] Create queue service in frontend/src/app/services/api/queue.service.ts
- [ ] T075 [P] [US4] Create business service in frontend/src/app/services/api/business.service.ts
- [ ] T076 [P] [US4] Setup Ngrx actions for queue management in frontend/src/app/store/queue/queue.actions.ts
- [ ] T077 [P] [US4] Setup Ngrx reducers for queue management in frontend/src/app/store/queue/queue.reducer.ts
- [ ] T078 [P] [US4] Setup Ngrx selectors for queue management in frontend/src/app/store/queue/queue.selectors.ts
- [ ] T079 [P] [US4] Setup Ngrx effects for queue management in frontend/src/app/store/queue/queue.effects.ts
- [ ] T080 [P] [US4] Create queue management page in frontend/src/app/pages/queue-management/queue-management.component.ts
- [ ] T081 [P] [US4] Create dashboard page in frontend/src/app/pages/dashboard/dashboard.component.ts
- [ ] T082 [US4] Implement mobile-responsive UI with Angular Material
- [ ] T083 [US4] Add form validation for business operations
- [ ] T084 [US4] Add error handling and user feedback
- [ ] T085 [US4] Add loading states and user experience improvements

**Checkpoint**: All user stories should now be independently functional

---

## Phase 7: Polish & Cross-Cutting Concerns

**Purpose**: Improvements that affect multiple user stories

- [ ] T086 [P] Add comprehensive API documentation with OpenAPI in backend/
- [ ] T087 [P] Add frontend component documentation in frontend/
- [ ] T088 Code cleanup and refactoring across all modules
- [ ] T089 Performance optimization for Redis operations
- [ ] T090 [P] Add unit tests for all services in backend/src/test/
- [ ] T091 [P] Add component tests for all frontend components in frontend/src/
- [ ] T092 Security hardening for WhatsApp webhook validation
- [ ] T093 Add monitoring and health checks in backend/
- [ ] T094 Run quickstart.md validation and update documentation
- [ ] T095 Add Docker production configuration
- [ ] T096 Add environment-specific configurations

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: Depends on Setup completion - BLOCKS all user stories
- **User Stories (Phase 3-6)**: All depend on Foundational phase completion
  - User stories can then proceed in parallel (if staffed)
  - Or sequentially in priority order (P1 → P2 → P3 → P4)
- **Polish (Phase 7)**: Depends on all desired user stories being complete

### User Story Dependencies

- **User Story 1 (P1)**: Can start after Foundational (Phase 2) - No dependencies on other stories
- **User Story 2 (P2)**: Can start after Foundational (Phase 2) - Extends US1 notification system
- **User Story 3 (P3)**: Can start after Foundational (Phase 2) - Extends US1 queue operations
- **User Story 4 (P4)**: Can start after Foundational (Phase 2) - Independent admin interface

### Within Each User Story

- Tests MUST be written and FAIL before implementation
- Entities before repositories
- Repositories before services
- Services before controllers
- Core implementation before integration
- Story complete before moving to next priority

### Parallel Opportunities

- All Setup tasks marked [P] can run in parallel
- All Foundational tasks marked [P] can run in parallel (within Phase 2)
- Once Foundational phase completes, all user stories can start in parallel (if team capacity allows)
- All tests for a user story marked [P] can run in parallel
- Entities within a story marked [P] can run in parallel
- Frontend and backend tasks for US4 can run in parallel

---

## Parallel Example: User Story 1

```bash
# Launch all tests for User Story 1 together:
Task: "Unit test for Business entity in backend/src/test/java/com/example/whatsappqueue/domain/BusinessTest.java"
Task: "Unit test for QueueEntry entity in backend/src/test/java/com/example/whatsappqueue/domain/QueueEntryTest.java"
Task: "Integration test for WhatsApp webhook in backend/src/test/java/com/example/whatsappqueue/api/WhatsAppWebhookControllerTest.java"
Task: "Contract test for join queue endpoint in backend/src/test/java/com/example/whatsappqueue/api/QueueControllerTest.java"

# Launch all entities for User Story 1 together:
Task: "Create Business entity in backend/src/main/java/com/example/whatsappqueue/domain/Business.java"
Task: "Create QueueEntry entity in backend/src/main/java/com/example/whatsappqueue/domain/QueueEntry.java"
Task: "Create Notification entity in backend/src/main/java/com/example/whatsappqueue/domain/Notification.java"
Task: "Create QueueStateChange entity in backend/src/main/java/com/example/whatsappqueue/domain/QueueStateChange.java"
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup
2. Complete Phase 2: Foundational (CRITICAL - blocks all stories)
3. Complete Phase 3: User Story 1
4. **STOP and VALIDATE**: Test User Story 1 independently
5. Deploy/demo if ready

### Incremental Delivery

1. Complete Setup + Foundational → Foundation ready
2. Add User Story 1 → Test independently → Deploy/Demo (MVP!)
3. Add User Story 2 → Test independently → Deploy/Demo
4. Add User Story 3 → Test independently → Deploy/Demo
5. Add User Story 4 → Test independently → Deploy/Demo
6. Each story adds value without breaking previous stories

### Parallel Team Strategy

With multiple developers:

1. Team completes Setup + Foundational together
2. Once Foundational is done:
   - Developer A: User Story 1 (Backend focus)
   - Developer B: User Story 2 (Backend focus)
   - Developer C: User Story 3 (Backend focus)
   - Developer D: User Story 4 (Frontend focus)
3. Stories complete and integrate independently

---

## Task Summary

- **Total Tasks**: 96
- **Setup Phase**: 7 tasks
- **Foundational Phase**: 11 tasks (BLOCKS all stories)
- **User Story 1**: 23 tasks (MVP)
- **User Story 2**: 10 tasks
- **User Story 3**: 8 tasks
- **User Story 4**: 25 tasks (13 backend + 12 frontend)
- **Polish Phase**: 11 tasks

### Parallel Opportunities Identified

- **Setup**: 6 parallel tasks
- **Foundational**: 8 parallel tasks
- **US1**: 8 parallel tests + 8 parallel entities
- **US2**: 2 parallel tests
- **US3**: 2 parallel tests
- **US4**: 12 parallel frontend + 8 parallel backend tasks

### Independent Test Criteria

- **US1**: WhatsApp join → position/ETA response within 10 seconds
- **US2**: Queue advancement → automatic notifications to affected customers
- **US3**: Leave request → removal confirmation + position updates
- **US4**: Admin actions → queue state changes + WhatsApp notifications

---

## Notes

- [P] tasks = different files, no dependencies
- [Story] label maps task to specific user story for traceability
- Each user story should be independently completable and testable
- Verify tests fail before implementing
- Commit after each task or logical group
- Stop at any checkpoint to validate story independently
- Avoid: vague tasks, same file conflicts, cross-story dependencies that break independence
