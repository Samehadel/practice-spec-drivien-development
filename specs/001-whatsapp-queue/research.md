# Phase 0 Research: WhatsApp Virtual Queue

**Feature**: WhatsApp Virtual Queue (MVP)  
**Date**: 2026-01-27  
**Purpose**: Resolve technical unknowns and establish best practices for implementation

## Research Findings

### WhatsApp Integration Provider

**Decision**: Meta WhatsApp Business Cloud API  
**Rationale**: 
- Official API from Meta with comprehensive documentation
- Direct integration without third-party markup costs
- Supports webhook-based message handling (ideal for real-time queue updates)
- Scalable pricing model for MVP growth
- Rich message formatting support for queue notifications

**Alternatives Considered**:
- Twilio WhatsApp API: Higher costs, additional vendor dependency
- MessageBird: Less mature WhatsApp integration
- Custom WhatsApp solutions: Not feasible due to WhatsApp's closed ecosystem

### Caching Strategy

**Decision**: Redis for real-time queue state management  
**Rationale**:
- Queue state changes frequently and needs low-latency access
- Redis provides O(1) complexity for queue operations (LPUSH, RPOP, LINDEX)
- Enables real-time notifications without database round trips
- Supports pub/sub for WebSocket updates to admin interface
- Persistent queue state can be rebuilt from PostgreSQL on restart

**Implementation Pattern**:
- Redis: Current active queue state, positions, ETAs
- PostgreSQL: Audit trail, business configuration, historical data

### Frontend Framework

**Decision**: Angular 17+ with standalone components  
**Rationale**:
- Aligns with project constitution and existing skill set
- Standalone components reduce boilerplate for MVP
- Built-in reactive forms support (constitution requirement)
- Strong TypeScript integration for type safety
- Comprehensive testing support with Jasmine/Karma

**Testing Approach**: Jasmine + Karma for unit tests, Cypress for E2E tests
- **Ngrx Testing**: MockStore for unit testing reducers and effects
- **Signals Testing**: TestBed with Signal testing utilities
- **Component Testing**: Isolated component testing with signal inputs/outputs

### Real-time Updates

**Decision**: No real-time updates needed for frontend admin dashboard  
**Rationale**:
- Admin dashboard is the source of queue state changes (serve, no-show, skip)
- Frontend only sends actions to backend, doesn't need to receive updates
- WhatsApp customers receive notifications via webhook responses
- Simplifies architecture and removes WebSocket complexity

**Implementation**: Standard HTTP REST API calls from Angular to Spring Boot backend

## Technology Stack Summary

### Backend
- **Java 17** with Spring Boot 3.x
- **Spring Data JPA** with PostgreSQL for persistence
- **Redis** for real-time queue state and caching
- **MapStruct** for object mapping
- **Liquibase** for database migrations
- **Meta WhatsApp Business Cloud API** for messaging
- **JUnit 5 + Mockito + TestContainers** for testing

### Frontend
- **Angular 17+** with standalone components
- **Angular Signals** for reactive change detection
- **Ngrx** for state management
- **Reactive Forms** for form handling
- **RxJS** for reactive programming
- **Angular Material** for UI components
- **Jasmine + Karma** for unit tests
- **Cypress** for E2E tests

### Infrastructure
- **Docker Compose** for local development
- **PostgreSQL** for primary data storage
- **Redis** for caching and real-time state
- **NGINX** (optional) for production deployment

## Frontend Architecture

### State Management with Ngrx

**Decision**: Ngrx for centralized state management  
**Rationale**:
- Predictable state container for queue data
- Time-travel debugging for complex queue interactions
- Optimistic updates for better UX
- Separation of side effects from components
- Consistent state across WebSocket updates

**Store Structure**:
```typescript
interface AppState {
  queue: QueueState;
  business: BusinessState;
  ui: UIState;
  router: RouterState;
}

interface QueueState {
  entries: QueueEntry[];
  currentPosition: number;
  estimatedWaitTime: number;
  loading: boolean;
  error: string | null;
}
```

### Reactive Change Detection with Signals

**Decision**: Angular Signals for fine-grained reactivity  
**Rationale**:
- Automatic dependency tracking
- Better performance than Zone.js for frequent form updates
- Simplified component logic
- Excellent for form validation and user interactions

**Signal Usage**:
- Component-local state (form inputs, UI state)
- Computed values derived from Ngrx store
- Form validation and user interactions
- HTTP request/response handling

### Component Architecture

**Pattern**: Smart/Dumb components with Signals + Ngrx

```typescript
// Smart component - connects to Ngrx
@Component({
  standalone: true,
  template: `
    <queue-display 
      [entries]="entries()"
      [currentPosition]="currentPosition()"
      (advanceQueue)="onAdvanceQueue()" />
  `
})
export class QueueManagementComponent {
  entries = this.store.selectSignal(selectQueueEntries);
  currentPosition = this.store.selectSignal(selectCurrentPosition);
  
  constructor(private store: Store) {}
  
  onAdvanceQueue() {
    this.store.dispatch(QueueActions.advanceQueue());
  }
}

// Dumb component - uses signals for local state
@Component({
  standalone: true
})
export class QueueDisplayComponent {
  @Input() entries: QueueEntry[] = [];
  @Input() currentPosition: number = 0;
  @Output() advanceQueue = new EventEmitter<void>();
  
  selectedEntry = signal<QueueEntry | null>(null);
  isProcessing = signal(false);
}
```

## Performance Considerations

### WhatsApp Message Processing
- Target: <10 second response time for 95% of messages
- Strategy: Asynchronous processing with Redis queue
- Monitoring: Custom metrics for message processing latency

### Queue Operations
- Target: O(1) complexity for join/leave/position queries
- Strategy: Redis sorted sets with timestamp-based scoring
- Monitoring: Queue size and operation latency metrics

### Admin Interface
- Target: <3 clicks for queue management actions
- Strategy: Optimized mobile-first UI with WebSocket updates
- Monitoring: Frontend performance metrics and user interaction tracking

## Security Considerations

### WhatsApp Integration
- Webhook signature verification for Meta API
- Rate limiting to prevent abuse
- Secure storage of API credentials

### Data Protection
- PII minimization (store only necessary customer data)
- GDPR compliance considerations for EU customers
- Secure communication between frontend and backend

## Next Steps

With research complete, proceed to Phase 1:
1. Design data model based on research decisions
2. Generate API contracts
3. Create quickstart guide
4. Update agent context with new technologies
