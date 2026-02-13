# Response Handling Architecture

This document describes how responses are handled in this Spring Boot application, focusing on the unified response structure and centralized response wrapping mechanism.

## Overview

The application implements a unified response handling system that ensures all API responses follow a consistent structure. This is achieved through two main components:

1. **`ApiResponse<T>`** - A generic DTO that standardizes the response format
2. **`ResponseAdvice`** - A centralized interceptor that automatically wraps responses

## Core Components

### 1. ApiResponse DTO

**Location**: `src/main/java/com/example/initial/common/dto/ApiResponse.java`

The `ApiResponse<T>` class provides a standardized response structure with the following fields:

```java
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private ResponseStatus status;        // SUCCESS or FAILED
    private String code;                   // Error code (e.g., "AUTH_001")
    private String errorReference;         // Unique error reference for tracking
    private String message;                // Human-readable message
    private T data;                        // Response payload
    private List<String> errors;           // Validation errors list
    private final LocalDateTime timestamp; // Response timestamp
}
```

**Key Features**:
- **Generic Type Support**: `<T>` allows any data type to be wrapped
- **Null Exclusion**: `@JsonInclude(NON_NULL)` excludes null fields from JSON
- **Auto Timestamp**: Automatically includes response timestamp
- **Flexible Structure**: Supports both success and error responses

### 2. ResponseAdvice Interceptor

**Location**: `src/main/java/com/example/initial/common/config/ResponseAdvice.java`

The `ResponseAdvice` class implements Spring's `ResponseBodyAdvice` to automatically wrap controller responses:

```java
@RestControllerAdvice
@RequiredArgsConstructor
public class ResponseAdvice implements ResponseBodyAdvice<Object> {
    private final ResponseBuilderService responseBuilder;
    
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        // Determines when to apply wrapping
    }
    
    @Override
    public Object beforeBodyWrite(Object body, ...) {
        // Wraps the response body
    }
}
```

**Wrapping Logic**:
- **Automatic Wrapping**: All controller responses are automatically wrapped unless excluded
- **Exclusion Conditions**: Responses are NOT wrapped if they are:
  - Already an `ApiResponse` object
  - `ResponseEntity` objects
  - `Resource` objects (for file downloads)
  - Annotated with `@IgnoreResponseWrapper`

### 3. ResponseBuilderService

**Location**: `src/main/java/com/example/initial/common/service/impl/ResponseBuilderService.java`

This service provides convenient methods for creating standardized responses:

```java
@Service
@RequiredArgsConstructor
public class ResponseBuilderService {
    private final MessageSourceService messageSourceService;
    
    // Success responses
    public <T> ApiResponse<T> success(final T data)
    
    // Error responses
    public <T> ApiResponse<T> error(final ApplicationError applicationError)
    public <T> ApiResponse<T> error(final ApplicationError applicationError, final String reference, final Object... args)
    public <T> ApiResponse<T> error(final ApplicationError applicationError, final List<String> errors)
    
    // Security responses
    public <T> ApiResponse<T> accessDenied()
    public ApiResponse<Void> unauthorized()
}
```

**Features**:
- **Message Localization**: Uses `MessageSourceService` for internationalized messages
- **Error Code Mapping**: Maps `ApplicationError` enums to response codes
- **Flexible Error Handling**: Supports various error scenarios with references and validation errors

## Response Formats

### Success Response

```json
{
  "status": "SUCCESS",
  "data": {
    "id": 1,
    "name": "John Doe",
    "email": "john@example.com"
  },
  "timestamp": "2024-02-14T10:30:00"
}
```

### Error Response

```json
{
  "status": "FAILED",
  "code": "AUTH_001",
  "message": "Invalid credentials",
  "errorReference": "ERR-123456",
  "timestamp": "2024-02-14T10:30:00"
}
```

### Validation Error Response

```json
{
  "status": "FAILED",
  "code": "VAL_001",
  "message": "Validation failed",
  "errors": [
    "Email is required",
    "Password must be at least 8 characters"
  ],
  "timestamp": "2024-02-14T10:30:00"
}
```

## Error Handling System

### ApplicationError Enum

**Location**: `src/main/java/com/example/initial/common/enums/ApplicationError.java`

Defines standardized error codes with message keys:

```java
public enum ApplicationError {
    INVALID_CREDENTIALS("AUTH_001", "auth.login.failed", "Invalid credentials. [%s]"),
    ACCESS_DENIED("AUTH_002", "auth.access.denied", "User does not have permission. [%s]"),
    RESOURCE_NOT_FOUND("RES_001", "resource.not.found", "Resource not found. [%s]"),
    // ... more error types
}
```

**Error Categories**:
- **Authentication Errors** (`AUTH_*`): Login, token, permission issues
- **Validation Errors** (`VAL_*`): Input validation failures
- **Resource Errors** (`RES_*`): Entity not found, already exists
- **System Errors** (`SYS_*`): Internal system failures

## Usage Examples

### Controller Implementation

```java
@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {
        // This response will be automatically wrapped
        return userService.findById(id);
    }
    
    @PostMapping
    @IgnoreResponseWrapper  // This response won't be wrapped
    public ResponseEntity<Void> createUser(@RequestBody User user) {
        userService.create(user);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
```

### Manual Response Creation

```java
@Service
public class UserService {
    
    public ApiResponse<User> getUser(Long id) {
        User user = userRepository.findById(id);
        return responseBuilder.success(user);
    }
    
    public ApiResponse<Void> deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            return responseBuilder.error(ApplicationError.RESOURCE_NOT_FOUND);
        }
        userRepository.deleteById(id);
        return responseBuilder.success(null);
    }
}
```

## Configuration and Customization

### Excluding Endpoints from Wrapping

Use the `@IgnoreResponseWrapper` annotation to exclude specific endpoints:

```java
@IgnoreResponseWrapper  // Class-level exclusion
@RestController
public class FileController {
    
    @IgnoreResponseWrapper  // Method-level exclusion
    @GetMapping("/download")
    public Resource downloadFile() {
        return fileService.getFile();
    }
}
```

### Custom Error Handling

Create custom error types by extending the `ApplicationError` enum:

```java
public enum ApplicationError {
    // Existing errors...
    CUSTOM_BUSINESS_ERROR("BIZ_001", "business.error", "Custom business logic error. [%s]");
}
```

## Benefits

1. **Consistency**: All API responses follow the same structure
2. **Maintainability**: Centralized response handling reduces code duplication
3. **Internationalization**: Built-in support for localized messages
4. **Error Tracking**: Unique error references for debugging
5. **Flexibility**: Easy to exclude specific endpoints when needed
6. **Type Safety**: Generic type support ensures compile-time safety

## Best Practices

1. **Use ResponseBuilderService**: Leverage the service for creating responses instead of manual construction
2. **Proper Error Codes**: Use appropriate `ApplicationError` enums for different error scenarios
3. **Localization**: Define message keys in properties files for internationalization
4. **Selective Exclusion**: Only exclude endpoints that truly need custom response formats
5. **Consistent Timestamps**: Rely on the automatic timestamp generation
6. **Validation Errors**: Use the `errors` field for multiple validation messages

## Integration Points

- **Exception Handlers**: Global exception handlers should use `ResponseBuilderService` for error responses
- **Security Layer**: Authentication/authorization failures use predefined error responses
- **Validation Framework**: Integration with validation frameworks to populate the `errors` field
- **Logging**: Error references should be logged for debugging and monitoring

This response handling system provides a robust, consistent, and maintainable way to manage API responses across the entire application.
