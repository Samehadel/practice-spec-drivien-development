# Exception Handling Design Documentation

## Overview

This document describes the comprehensive exception handling design implemented in this Spring Boot application. The system provides a structured approach to error handling with custom exception types, centralized exception handling, and consistent error responses.

## Architecture

### Exception Hierarchy

```
BaseException (Abstract)
├── AuthorizationException
├── InternalException
├── InvalidCredentialsException
├── ResourceAlreadyExistsException
├── ResourceNotFoundException
├── TokenException
├── UnavailableServiceException
└── ValidationException
```

### Base Exception Class

The `BaseException` class serves as the foundation for all custom exceptions in the application:

```java
public abstract class BaseException extends RuntimeException {
    private final ApplicationError applicationError;
    private final String reference;
    private final HttpStatus status;
    private final Object[] args;
    private final String message;
    private final Exception cause;
}
```

**Key Features:**
- Extends `RuntimeException` for unchecked exception behavior
- Contains standardized error information (code, message, status)
- Supports message arguments for dynamic content
- Includes reference tracking for debugging
- Maintains cause chain for root cause analysis

## Exception Types

### 1. AuthorizationException
- **Purpose**: Handles authorization and permission-related errors
- **HTTP Status**: 403 Forbidden
- **Use Cases**: User lacks permission to access resources or perform actions

### 2. TokenException
- **Purpose**: Manages authentication token-related errors
- **HTTP Status**: 401 Unauthorized
- **Token Types**: ACCESS, REFRESH
- **Error Types**: Invalid, Expired, Idle, Mismatch
- **Use Cases**: Token validation failures, expiration, format issues

### 3. ValidationException
- **Purpose**: Handles input validation errors
- **HTTP Status**: 400 Bad Request
- **Use Cases**: Missing required fields, invalid data formats, custom validation rules

### 4. ResourceNotFoundException
- **Purpose**: Handles missing resource scenarios
- **HTTP Status**: 404 Not Found
- **Use Cases**: Database records not found, file not available, endpoint not existing

### 5. ResourceAlreadyExistsException
- **Purpose**: Handles duplicate resource creation attempts
- **HTTP Status**: 409 Conflict
- **Use Cases**: Duplicate usernames, email addresses, unique constraint violations

### 6. InternalException
- **Purpose**: Manages internal system errors
- **HTTP Status**: 500 Internal Server Error
- **Features**: Auto-generates error references for tracking
- **Use Cases**: Database errors, external service failures, unexpected system errors

### 7. InvalidCredentialsException
- **Purpose**: Handles authentication credential failures
- **HTTP Status**: 401 Unauthorized
- **Use Cases**: Login failures, password mismatches

### 8. UnavailableServiceException
- **Purpose**: Handles external service unavailability
- **HTTP Status**: 503 Service Unavailable
- **Use Cases**: Third-party API failures, database connection issues

## ApplicationError Enum

The `ApplicationError` enum provides standardized error definitions:

```java
public enum ApplicationError {
    // Auth errors (AUTH_001 to AUTH_009)
    INVALID_CREDENTIALS("AUTH_001", "auth.login.failed", "Invalid credentials. [%s]"),
    ACCESS_DENIED("AUTH_002", "auth.access.denied", "User does not have permission..."),
    TOKEN_EXPIRED("AUTH_003", "auth.token.expired", "Authentication token has expired..."),
    
    // Validation errors (VAL_001 to VAL_003)
    VALIDATION_ERROR("VAL_001", "validation.error", "A validation error occurred..."),
    MISSING_REQUIRED_FIELD("VAL_002", "validation.missing.required.field", "A required field..."),
    
    // Resource errors (RES_001 to RES_002)
    RESOURCE_NOT_FOUND("RES_001", "resource.not.found", "The requested resource..."),
    RESOURCE_ALREADY_EXISTS("RES_002", "resource.already.exists", "The resource already..."),
    
    // System errors (SYS_001 to SYS_006)
    INTERNAL_ERROR("SYS_001", "system.error", "An internal system error occurred..."),
    // ... more system errors
}
```

**Structure:**
- **Code**: Unique error identifier (e.g., "AUTH_001")
- **Message Key**: Internationalization key (e.g., "auth.login.failed")
- **Message**: Default error message with placeholder support

## ExceptionService: Centralized Exception Building

The `ExceptionService` class provides builder patterns for creating exceptions consistently:

### TokenExceptionBuilder
```java
public static class TokenExceptionBuilder {
    public static TokenException invalidRefreshTokenException()
    public static TokenException invalidTokenException(TokenType tokenType, String message)
    public static TokenException expiredTokenException(TokenType tokenType)
    public static TokenException expiredRefreshTokenException()
    public static TokenException expiredAccessTokenException()
}
```

### ValidationExceptionBuilder
```java
public static class ValidationExceptionBuilder {
    public static ValidationException validationException(String property)
    public static ValidationException validationExceptionWithMessage(String message)
}
```

### InternalExceptionBuilder
```java
public static class InternalExceptionBuilder {
    public static InternalException internalExceptionWithReference(ApplicationError error, Exception cause, Object... args)
    public static InternalException internalExceptionWithReference(ApplicationError error, Object... args)
    public static InternalException internalExceptionWithReference(Exception cause)
    public static InternalException genericInternalExceptionWithReference()
}
```

### ResourceExceptionBuilder
```java
public static class ResourceExceptionBuilder {
    public static ResourceNotFoundException resourceNotFoundException(String resource, Object id)
    public static ResourceAlreadyExistsException resourceAlreadyExistsException(String resource, String username)
}
```

### AuthorizationExceptionBuilder
```java
public static class AuthorizationExceptionBuilder {
    public static AuthorizationException authorizationException(ApplicationError applicationError)
}
```

## GlobalExceptionHandler

The `GlobalExceptionHandler` provides centralized exception handling using `@RestControllerAdvice`:

### Key Features

1. **Specific Exception Handlers**: Each custom exception type has a dedicated handler
2. **Standard Spring Exceptions**: Handles common Spring validation and security exceptions
3. **Fallback Handler**: Catches all unexpected exceptions
4. **Consistent Response Format**: All handlers return `ApiResponse<Void>` with standardized error structure
5. **Logging**: Comprehensive error logging with references for debugging

### Exception Handler Methods

```java
@ExceptionHandler(AuthorizationException.class)
public ResponseEntity<ApiResponse<Void>> handleAuthorizationException(AuthorizationException ex)

@ExceptionHandler(TokenException.class)
public ResponseEntity<ApiResponse<Void>> handleTokenException(TokenException ex)

@ExceptionHandler(InternalException.class)
public ResponseEntity<ApiResponse<Void>> handleInternalException(InternalException ex)

@ExceptionHandler(ResourceAlreadyExistsException.class)
public ResponseEntity<ApiResponse<Void>> handleResourceAlreadyExistsException(ResourceAlreadyExistsException ex)

@ExceptionHandler(ResourceNotFoundException.class)
public ResponseEntity<ApiResponse<Void>> handleResourceNotFoundException(ResourceNotFoundException ex)

@ExceptionHandler(ValidationException.class)
public ResponseEntity<ApiResponse<Void>> handleValidationException(ValidationException ex)

@ExceptionHandler(InvalidCredentialsException.class)
public ResponseEntity<ApiResponse<Void>> handleInvalidCredentialsException(InvalidCredentialsException ex)
```

### Spring Framework Exception Handlers

```java
@ExceptionHandler(MethodArgumentNotValidException.class)
public ResponseEntity<ApiResponse<Void>> handleValidationErrors(MethodArgumentNotValidException ex)

@ExceptionHandler(ConstraintViolationException.class)
public ResponseEntity<ApiResponse<Void>> handleConstraintViolation(ConstraintViolationException ex)

@ExceptionHandler(AccessDeniedException.class)
public ResponseEntity<ApiResponse<Void>> handleAccessDeniedException(AccessDeniedException ex)

@ExceptionHandler(DataIntegrityViolationException.class)
public ResponseEntity<ApiResponse<Void>> handleDataIntegrityViolationException(DataIntegrityViolationException ex)

@ExceptionHandler(InvalidDataAccessResourceUsageException.class)
public ResponseEntity<ApiResponse<Void>> handleDataIntegrityViolationException(InvalidDataAccessResourceUsageException ex)

@ExceptionHandler(Exception.class)
public ResponseEntity<ApiResponse<Void>> handleUnknownErrors(Exception ex)
```

## Usage Patterns

### 1. Throwing Exceptions Using ExceptionService

```java
// Token exceptions
throw ExceptionService.TokenExceptionBuilder.expiredAccessTokenException();
throw ExceptionService.TokenExceptionBuilder.invalidTokenException(TokenType.REFRESH, "Invalid format");

// Validation exceptions
throw ExceptionService.ValidationExceptionBuilder.validationException("email");
throw ExceptionService.ValidationExceptionBuilder.validationExceptionWithMessage("Custom validation message");

// Internal exceptions
throw ExceptionService.InternalExceptionBuilder.internalExceptionWithReference(
    ApplicationError.EMAIL_SEND_FAILED, 
    smtpException, 
    "user@example.com"
);

// Resource exceptions
throw ExceptionService.ResourceExceptionBuilder.resourceNotFoundException("User", userId);
throw ExceptionService.ResourceExceptionBuilder.resourceAlreadyExistsException("User", username);

// Authorization exceptions
throw ExceptionService.AuthorizationExceptionBuilder.authorizationException(
    ApplicationError.ACCESS_DENIED
);
```

### 2. Direct Exception Creation

```java
// Token exceptions
throw TokenException.expired(TokenType.ACCESS);
throw TokenException.withMessage(TokenType.REFRESH, "Custom token error");

// Validation exceptions
throw ValidationException.missingProperty("password");
throw ValidationException.withMessage("Invalid email format");

// Internal exceptions
throw InternalException.internal(ApplicationError.CONFIGURATION_ERROR, configException);
throw InternalException.genericInternal();

// Resource exceptions
throw ResourceNotFoundException.withResource("Product", productId);
```

## Error Response Structure

All exceptions return a standardized `ApiResponse<Void>` structure:

```json
{
  "success": false,
  "error": {
    "code": "AUTH_001",
    "message": "Invalid credentials. [Additional context]",
    "reference": "ABC123XYZ",
    "timestamp": "2023-01-01T12:00:00Z"
  },
  "data": null
}
```

## Best Practices

### 1. Exception Selection
- Use `ValidationException` for input validation failures
- Use `ResourceNotFoundException` for missing resources
- Use `ResourceAlreadyExistsException` for duplicate creation attempts
- Use `TokenException` for authentication token issues
- Use `AuthorizationException` for permission problems
- Use `InternalException` for unexpected system errors

### 2. ExceptionService Usage
- Prefer `ExceptionService` builders for consistency
- Use specific builders rather than direct exception creation
- Include relevant context and arguments

### 3. Error References
- Internal exceptions auto-generate references for tracking
- Include meaningful context in exception arguments
- Use references for debugging and support

### 4. Logging
- All exceptions are logged with full context
- Include error references in log messages
- Use appropriate log levels (ERROR for exceptions)

### 5. Internationalization
- Error messages support internationalization via message keys
- Default messages serve as fallbacks
- Consider locale-specific message formatting

## Implementation Guidelines

### Adding New Exception Types

1. **Create Exception Class**:
```java
public class NewException extends BaseException {
    private NewException(ApplicationError applicationError, String reference, Object... args) {
        super(applicationError, HttpStatus.BAD_REQUEST, reference, null, args);
    }
    
    public static NewException withContext(String context) {
        return new NewException(ApplicationError.NEW_ERROR, null, context);
    }
}
```

2. **Add ApplicationError**:
```java
NEW_ERROR("CAT_001", "new.error.key", "New error occurred. [%s]")
```

3. **Add ExceptionService Builder**:
```java
public static class NewExceptionBuilder {
    public static NewException newException(String context) {
        return NewException.withContext(context);
    }
}
```

4. **Add Exception Handler**:
```java
@ExceptionHandler(NewException.class)
public ResponseEntity<ApiResponse<Void>> handleNewException(NewException ex) {
    log.error("New exception occurred: [{}] with reference: [{}]", ex.getMessage(), ex.getReference(), ex);
    ApiResponse<Void> errorResponse = buildErrorResponse(ex);
    return ResponseEntity.status(ex.getStatus()).body(errorResponse);
}
```

### Error Response Customization

The response format can be customized by modifying the `ResponseBuilderService` and `ApiResponse` structure. Ensure consistency across all exception handlers.

## Testing Exception Handling

### Unit Testing Exceptions

```java
@Test
void testTokenException() {
    TokenException exception = ExceptionService.TokenExceptionBuilder.expiredAccessTokenException();
    
    assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
    assertEquals(ApplicationError.TOKEN_EXPIRED, exception.getApplicationError());
    assertEquals(TokenType.ACCESS, exception.getTokenType());
}

@Test
void testGlobalExceptionHandler() {
    TokenException exception = TokenException.expired(TokenType.ACCESS);
    
    ResponseEntity<ApiResponse<Void>> response = globalExceptionHandler.handleTokenException(exception);
    
    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    assertFalse(response.getBody().isSuccess());
    assertEquals("AUTH_003", response.getBody().getError().getCode());
}
```

### Integration Testing

Test exception scenarios through API endpoints to ensure proper error propagation and response formatting.

## Conclusion

This exception handling design provides a robust, maintainable, and consistent approach to error management in the Spring Boot application. The centralized ExceptionService, comprehensive exception hierarchy, and standardized GlobalExceptionHandler ensure that errors are handled uniformly across the entire application.

The design supports:
- Consistent error responses
- Proper HTTP status codes
- Comprehensive logging and debugging
- Internationalization support
- Easy extension and maintenance

Follow the documented patterns and best practices when implementing new error scenarios or modifying existing exception handling logic.
