# Quickstart Guide: WhatsApp Virtual Queue

**Feature**: WhatsApp Virtual Queue (MVP)  
**Date**: 2026-01-27  
**Technologies**: Java 21, Spring Boot 3.x, Angular 17+, PostgreSQL, Redis

## Prerequisites

### Development Environment
- **Java 21** or later
- **Node.js 18** or later
- **Docker** and **Docker Compose**
- **Maven 3.8** or later
- **Angular CLI 17** or later
- **PostgreSQL client** (optional, for direct DB access)
- **Redis CLI** (optional, for cache inspection)

### External Services
- **Meta WhatsApp Business API** access
- **Domain name** for webhook endpoints (development: ngrok)

## Project Setup

### 1. Clone Repository
```bash
git clone <repository-url>
cd practice_spec_driven_development
git checkout 001-whatsapp-queue
```

### 2. Start Infrastructure Services
```bash
# Start PostgreSQL and Redis
docker-compose up -d

# Verify services are running
docker-compose ps
```

### 3. Backend Setup

#### Database Configuration
```bash
# The database will be automatically created by Liquibase
# Verify connection:
./mvnw spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=default"
```

#### Environment Variables
Create `backend/src/main/resources/application-local.yml`:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/whatsapp_queue
    username: postgres
    password: postgres
  
  redis:
    host: localhost
    port: 6379

whatsapp:
  api:
    version: v18.0
    base-url: https://graph.facebook.com
    phone-number-id: ${WHATSAPP_PHONE_NUMBER_ID}
    access-token: ${WHATSAPP_ACCESS_TOKEN}
    webhook-secret: ${WHATSAPP_WEBHOOK_SECRET}

logging:
  level:
    com.example.whatsappqueue: DEBUG
    org.springframework.web: DEBUG
```

#### WhatsApp API Setup
1. Create Meta Developer Account
2. Create WhatsApp Business App
3. Get Phone Number ID and Access Token
4. Configure webhook URL (use ngrok for development)

#### Run Backend
```bash
cd backend
./mvnw spring-boot:run
```

### 4. Frontend Setup

#### Install Dependencies
```bash
cd frontend
npm install
```

#### Environment Configuration
Create `frontend/src/environments/environment.local.ts`:
```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/v1'
};
```

#### Run Frontend
```bash
ng serve
```

## Development Workflow

### 1. Database Migrations
```bash
# Create new migration
./mvnw liquibase:diff

# Apply migrations
./mvnw liquibase:update

# Rollback migration
./mvnw liquibase:rollbackCount -Dliquibase.rollbackCount=1
```

### 2. Testing
```bash
# Backend tests
./mvnw test

# Frontend tests
cd frontend
npm run test

# E2E tests
npm run e2e
```

### 3. Building for Production
```bash
# Backend
./mvnw clean package -Pprod

# Frontend
cd frontend
npm run build
```

## API Usage Examples

### Create Business
```bash
curl -X POST http://localhost:8080/v1/businesses \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{
    "name": "Test Restaurant",
    "serviceType": "Restaurant",
    "whatsappPhoneNumber": "+1234567890",
    "averageServiceTimeMinutes": 15,
    "notificationThreshold": 3
  }'
```

### Get Queue Status
```bash
curl -X GET http://localhost:8080/v1/businesses/{businessId}/queue \
  -H "Authorization: Bearer <token>"
```

### Advance Queue
```bash
curl -X POST http://localhost:8080/v1/businesses/{businessId}/queue/advance \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{
    "action": "serve",
    "notes": "Customer served successfully"
  }'
```

## WhatsApp Integration Testing

### 1. Development Webhook Setup
```bash
# Start ngrok for local testing
ngrok http 8080

# Configure webhook in Meta Dashboard
# Use ngrok URL: https://<random>.ngrok.io/webhooks/whatsapp
```

### 2. Test WhatsApp Messages
Send messages to your WhatsApp number:
- "Hi" or "Join" → Join queue
- "Status" → Get current position
- "Leave" → Leave queue

### 3. Monitor Logs
```bash
# Backend logs
tail -f backend/logs/application.log

# WhatsApp webhook logs
grep "WhatsApp" backend/logs/application.log
```

## Frontend Development

### Component Structure
```
src/app/
├── components/
│   ├── queue-display/
│   ├── queue-management/
│   └── business-settings/
├── pages/
│   ├── dashboard/
│   └── queue/
├── store/
│   ├── queue/
│   ├── business/
│   └── ui/
├── services/
│   └── api/
└── models/
    ├── queue.model.ts
    └── business.model.ts
```

### State Management (Ngrx)
```typescript
// Queue Actions
export const loadQueue = createAction('[Queue] Load Queue');
export const loadQueueSuccess = createAction(
  '[Queue] Load Queue Success',
  props<{ queue: QueueEntry[] }>()
);
export const advanceQueue = createAction('[Queue] Advance Queue');

// Queue Selector
export const selectQueueEntries = createSelector(
  selectQueueState,
  (state: QueueState) => state.entries
);

// Component Usage
export class QueueComponent {
  entries = this.store.selectSignal(selectQueueEntries);
  
  constructor(private store: Store) {}
  
  onAdvance() {
    this.store.dispatch(advanceQueue());
  }
}
```

### Signals Usage
```typescript
export class QueueEntryComponent {
  @Input() entry!: QueueEntry;
  isExpanded = signal(false);
  
  expandedContent = computed(() => {
    return this.isExpanded() ? this.entry.details : null;
  });
  
  toggleExpanded() {
    this.isExpanded.set(!this.isExpanded());
  }
}
```

## Troubleshooting

### Common Issues

#### Database Connection
```bash
# Check PostgreSQL status
docker-compose logs postgres

# Test connection
psql -h localhost -U postgres -d whatsapp_queue
```

#### Redis Connection
```bash
# Check Redis status
docker-compose logs redis

# Test connection
redis-cli ping
```

#### WhatsApp Webhook
```bash
# Verify webhook configuration
curl -X POST https://graph.facebook.com/v18.0/me/subscribed_apps \
  -H "Authorization: Bearer <access-token>"
```

#### Frontend Build Issues
```bash
# Clear node modules
rm -rf node_modules package-lock.json
npm install

# Check Angular version compatibility
ng version
```

### Performance Monitoring

#### Backend Metrics
```bash
# Application metrics
curl http://localhost:8080/actuator/metrics

# Health check
curl http://localhost:8080/actuator/health
```

#### Frontend Performance
```bash
# Build analysis
npm run build -- --stats-json

# Lighthouse audit
npx lighthouse http://localhost:4200 --output html
```

## Deployment

### Docker Production Build
```bash
# Build all services
docker-compose -f docker-compose.prod.yml build

# Run production stack
docker-compose -f docker-compose.prod.yml up -d
```

### Environment Variables for Production
```yaml
# docker-compose.prod.yml
environment:
  - SPRING_PROFILES_ACTIVE=prod
  - SPRING_DATASOURCE_URL=${DATABASE_URL}
  - SPRING_DATASOURCE_USERNAME=${DATABASE_USERNAME}
  - SPRING_DATASOURCE_PASSWORD=${DATABASE_PASSWORD}
  - WHATSAPP_ACCESS_TOKEN=${WHATSAPP_ACCESS_TOKEN}
  - WHATSAPP_PHONE_NUMBER_ID=${WHATSAPP_PHONE_NUMBER_ID}
  - WHATSAPP_WEBHOOK_SECRET=${WHATSAPP_WEBHOOK_SECRET}
```

## Next Steps

1. **Configure WhatsApp Business API** with real phone number
2. **Set up CI/CD pipeline** for automated testing and deployment
3. **Configure monitoring** (Prometheus + Grafana)
4. **Set up log aggregation** (ELK stack)
5. **Implement backup strategy** for PostgreSQL
6. **Configure SSL certificates** for production

## Support

- **Backend Issues**: Check `backend/logs/application.log`
- **Frontend Issues**: Check browser console and network tab
- **Database Issues**: Check PostgreSQL logs and connection
- **WhatsApp Issues**: Verify Meta Developer Console settings

## Architecture Overview

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   WhatsApp      │    │   Angular App   │    │   Spring Boot   │
│   Customers     │◄──►│   (Ngrx/Signals)│◄──►│   Backend       │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                                │                        │
                                ▼                        ▼
                       ┌─────────────────┐    ┌─────────────────┐
                       │     Redis       │    │   PostgreSQL    │
                       │   (Queue State) │    │   (Audit Trail) │
                       └─────────────────┘    └─────────────────┘
```

This quickstart guide provides everything needed to get the WhatsApp Virtual Queue system running locally and ready for development.
