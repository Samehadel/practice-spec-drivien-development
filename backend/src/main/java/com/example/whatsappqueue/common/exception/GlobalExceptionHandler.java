package com.example.whatsappqueue.common.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @ExceptionHandler(ResourceNotFoundException.class)
    public Map<String, Object> handleResourceNotFoundException(
            ResourceNotFoundException ex,
            WebRequest request) {
        
        return createErrorResponse(
            HttpStatus.NOT_FOUND.value(),
            "Resource Not Found",
            ex.getMessage(),
            request.getDescription(false).replace("uri=", "")
        );
    }

    @ExceptionHandler(BusinessException.class)
    public Map<String, Object> handleBusinessException(
            BusinessException ex,
            WebRequest request) {
        
        return createErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "Business Rule Violation",
            ex.getMessage(),
            request.getDescription(false).replace("uri=", "")
        );
    }

    @ExceptionHandler(WhatsAppApiException.class)
    public Map<String, Object> handleWhatsAppApiException(
            WhatsAppApiException ex,
            WebRequest request) {
        
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        if (ex.getStatusCode() != 0) {
            status = HttpStatus.valueOf(ex.getStatusCode());
        }
        
        return createErrorResponse(
            status.value(),
            "WhatsApp API Error",
            ex.getMessage(),
            request.getDescription(false).replace("uri=", "")
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, Object> handleValidationException(
            MethodArgumentNotValidException ex,
            WebRequest request) {
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            errors.put(error.getField(), error.getDefaultMessage())
        );
        
        Map<String, Object> response = createErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "Validation Error",
            "Request validation failed",
            request.getDescription(false).replace("uri=", "")
        );
        response.put("errors", errors);
        
        return response;
    }

    @ExceptionHandler(AuthenticationException.class)
    public void handleAuthenticationException(
            AuthenticationException ex,
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        
        Map<String, Object> errorResponse = createErrorResponse(
            HttpStatus.UNAUTHORIZED.value(),
            "Authentication Failed",
            "Authentication is required",
            request.getServletPath()
        );
        
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        objectMapper.writeValue(response.getOutputStream(), errorResponse);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public void handleAccessDeniedException(
            AccessDeniedException ex,
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        
        Map<String, Object> errorResponse = createErrorResponse(
            HttpStatus.FORBIDDEN.value(),
            "Access Denied",
            "You don't have permission to access this resource",
            request.getServletPath()
        );
        
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.FORBIDDEN.value());
        objectMapper.writeValue(response.getOutputStream(), errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public Map<String, Object> handleGlobalException(
            Exception ex,
            WebRequest request) {
        
        return createErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Internal Server Error",
            "An unexpected error occurred",
            request.getDescription(false).replace("uri=", "")
        );
    }

    private Map<String, Object> createErrorResponse(
            int status,
            String error,
            String message,
            String path) {
        
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", Instant.now().toString());
        response.put("status", status);
        response.put("error", error);
        response.put("message", message);
        response.put("path", path);
        
        return response;
    }
}
