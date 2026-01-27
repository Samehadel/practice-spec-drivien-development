# Feature Specification: WhatsApp Virtual Queue (MVP)

**Feature Branch**: `001-whatsapp-queue`  
**Created**: 2026-01-22  
**Status**: Draft  
**Input**: WhatsApp-based virtual queue system that allows service businesses to manage walk-in customers digitally and give them real-time visibility into their waiting status—without hardware, mobile apps, or staff training.

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Join the queue via WhatsApp and see status (Priority: P1)

As a walk-in customer, I can join a business queue by sending a simple WhatsApp message and immediately receive my queue position and an estimated wait time.

**Why this priority**: Without a reliable join + confirmation flow, there is no queue.

**Independent Test**: Can be fully tested by sending the join message for a business with an open queue and verifying a confirmation message with a position and ETA is received.

**Acceptance Scenarios**:

1. **Given** the business queue is open, **When** a customer sends the configured “join” message, **Then** the system creates a queue entry and replies with the customer’s position number and an ETA.
2. **Given** the customer is already in the queue, **When** the customer sends the join message again, **Then** the system does not create a duplicate entry and replies with the customer’s current position and ETA.
3. **Given** the business queue is closed, **When** a customer sends the join message, **Then** the system replies that the queue is closed and does not add the customer.

---

### User Story 2 - Get proactive turn updates and notifications (Priority: P2)

As a queued customer, I automatically receive updates when the queue advances, including a “nearly your turn” message when I’m close.

**Why this priority**: Real-time visibility reduces anxiety and prevents missed turns.

**Independent Test**: Can be tested by creating a small queue, advancing the queue from the business side, and verifying customers receive status updates and “nearly your turn” notifications.

**Acceptance Scenarios**:

1. **Given** multiple customers are in the queue, **When** the business advances to the next customer, **Then** affected customers receive updated position and ETA.
2. **Given** a customer is within N positions of being served, **When** their position becomes N or less away from the front, **Then** the customer receives a “nearly your turn” message.
3. **Given** a customer becomes next in line, **When** the queue advances such that they are first, **Then** the customer receives a “you are next” message.

---

### User Story 3 - Leave the queue via WhatsApp (Priority: P3)

As a queued customer, I can leave the queue by sending a WhatsApp message and the queue updates accordingly.

**Why this priority**: Customers need a simple way to opt out; it also improves the accuracy of the queue.

**Independent Test**: Can be tested by joining, sending a “leave” message, verifying removal, and confirming remaining customers’ positions update.

**Acceptance Scenarios**:

1. **Given** a customer is currently in the queue, **When** the customer sends the configured “leave” message, **Then** the system removes the customer from the queue and confirms they have left.
2. **Given** a customer is not in the queue, **When** the customer sends the “leave” message, **Then** the system replies that they are not currently queued.

---

### User Story 4 - Business manages the queue from a minimal admin view (Priority: P4)

As a business operator, I can view the current queue and perform the minimum actions needed to serve customers in order (next, skip, mark served/no-show, open/close queue).

**Why this priority**: The business must be able to progress the queue, otherwise customers never get served.

**Independent Test**: Can be tested by creating a queue via customer join messages and using the admin interface to advance/skip/close while verifying the queue state changes.

**Acceptance Scenarios**:

1. **Given** a queue has customers, **When** the operator marks the current customer as served and advances to next, **Then** the system sets the served status and moves the “current” pointer to the next eligible customer.
2. **Given** the operator skips a customer, **When** the operator marks them as no-show or skipped, **Then** the system updates that entry’s status and moves to the next eligible customer.
3. **Given** the operator closes the queue, **When** a new customer attempts to join, **Then** the join is rejected with a “queue closed” response.

### Edge Cases

- Customer sends an unrecognized message (neither join nor leave).
- Customer joins, then the business closes the queue (existing entries remain; new joins rejected).
- Customer is “next” but does not respond/arrive; business marks as no-show.
- Duplicate messages or rapid repeated messages from the same customer.
- Customer joins multiple times with slightly different message text (case, whitespace).
- Business tries to advance when the queue is empty.
- Queue contains entries with non-active statuses (served/no-show/left) and should not be selected as current.
- ETA becomes negative or unrealistic; system clamps to a minimum of 0 and communicates “approximate” ETA.
- Business average service time is missing; ETA is calculated using a documented default average (e.g., 10 minutes) and clearly labeled as approximate until the business provides its average.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST support multiple businesses, each with its own independent queue.
- **FR-002**: System MUST associate each inbound customer message with exactly one business based on the WhatsApp number contacted.
- **FR-003**: System MUST allow a customer to join a business queue by sending a predefined message (e.g., “Hi” or “Join”).
- **FR-004**: When a customer joins successfully, the system MUST register the customer (at minimum by WhatsApp identifier) and create a queue entry.
- **FR-005**: When a customer joins successfully, the system MUST reply with the customer’s position number.
- **FR-006**: The system MUST provide an estimated waiting time (ETA) that is calculated using a simple, documented method and is presented as approximate.
- **FR-007**: The system MUST allow a customer to request their current position and ETA via WhatsApp (either via re-sending the join message or a status keyword).
- **FR-008**: The system MUST automatically notify affected customers when queue position changes due to business actions (next/skip/served/no-show/leave).
- **FR-009**: The system MUST send a “nearly your turn” notification when the customer is within N customers of being served.
- **FR-010**: The system MUST send a “you are next” notification when the customer becomes first in line.
- **FR-011**: The system MUST allow a customer to leave the queue via WhatsApp and MUST confirm the leave action.
- **FR-012**: The system MUST maintain FIFO ordering for active queue entries by default.
- **FR-013**: The system MUST provide a minimal business-facing interface that works on mobile browsers to:
  - View the current queue
  - Advance to the next customer
  - Skip a customer
  - Mark a customer as served
  - Mark a customer as no-show
  - Open the queue
  - Close the queue
- **FR-014**: The system MUST prevent queue actions that are inconsistent with the current state (e.g., “advance” when queue is empty) and provide a clear error message to the operator.
- **FR-015**: The system MUST support manual onboarding of businesses (no self-sign-up in MVP).
- **FR-016**: For each business, the system MUST store a minimal profile including:
  - Business name
  - Service type
  - Average service time (minutes)
- **FR-017**: The system MUST treat queue size limits as optional (out of scope unless explicitly enabled).
- **FR-018**: The system MUST keep an auditable history of queue entry state transitions (e.g., joined, served, no-show, left) for at least operational troubleshooting.

### Key Entities *(include if feature involves data)*

- **Business**: Represents a service business. Attributes include name, service type, WhatsApp number, queue open/closed state, average service time, and notification threshold N.
- **Customer**: Represents a person contacting the business via WhatsApp. Attributes include a stable messaging identifier and optional display name.
- **QueueEntry**: Represents a customer’s participation in a queue. Attributes include join time, current status (active/served/no-show/left), and ordering position among active entries.
- **QueueStateChange**: Represents an event that changes queue order or status (e.g., operator advanced, customer left). Used for auditability and triggering notifications.
- **Notification**: Represents an outbound customer message triggered by queue events (confirmation, status update, nearly-your-turn, you-are-next).

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: A customer can join the queue and receive confirmation (position + ETA) within 10 seconds of sending the join message in 95% of attempts under normal load.
- **SC-002**: When the business advances the queue, all affected customers receive an updated status notification within 10 seconds in 95% of cases under normal load.
- **SC-003**: In a controlled test queue of 10 customers, the ordering of active entries is always FIFO unless the business explicitly marks a customer as no-show/served.
- **SC-004**: The business operator can complete the “serve next customer” action (mark served + advance) in 3 taps/clicks or fewer from a mobile browser.
- **SC-005**: At least 90% of first-time users in a short usability test can successfully:
  - Join the queue
  - Understand their position
  - Understand when they are next
  - Leave the queue
  without staff assistance.
