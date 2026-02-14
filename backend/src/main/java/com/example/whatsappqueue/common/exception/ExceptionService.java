package com.example.whatsappqueue.common.exception;

import java.util.UUID;

import com.example.whatsappqueue.domain.ApplicationError;

public class ExceptionService {
    
    /**
     * Builds validation exceptions
     */
    public static class ValidationExceptionBuilder {
        
        public static ValidationException validationException(String property) {
            return new ValidationException("Validation failed for " + property);
        }
        
        public static ValidationException validationExceptionWithMessage(String message) {
            return new ValidationException(message);
        }
        
        public static ValidationException queueClosedException() {
            return new ValidationException("Queue is currently closed");
        }
        
        public static ValidationException customerAlreadyInQueueException(String whatsappIdentifier) {
            return new ValidationException("Customer is already in queue: " + whatsappIdentifier);
        }
        
        public static ValidationException invalidQueuePositionException() {
            return new ValidationException("Queue position must be a positive integer");
        }
        
        public static ValidationException invalidWaitTimeException() {
            return new ValidationException("Wait time must be non-negative");
        }
        
        public static ValidationException invalidBusinessServiceTimeException() {
            return new ValidationException("Service time must be between 1 and 120 minutes");
        }
        
        public static ValidationException invalidNotificationThresholdException() {
            return new ValidationException("Notification threshold must be between 1 and 10");
        }
        
        public static ValidationException invalidBusinessNameException() {
            return new ValidationException("Business name must be between 1 and 255 characters");
        }
        
        public static ValidationException invalidWhatsAppFormatException() {
            return new ValidationException("Invalid WhatsApp identifier format");
        }
    }
    
    /**
     * Builds resource exceptions
     */
    public static class ResourceExceptionBuilder {
        
        public static ResourceNotFoundException resourceNotFoundException(String resource, Object id) {
            return new ResourceNotFoundException(resource + " not found with " + id);
        }
        
        public static ResourceNotFoundException businessNotFoundException(UUID businessId) {
            return resourceNotFoundException("Business", businessId);
        }
        
        public static ResourceNotFoundException queueEntryNotFoundException(UUID queueEntryId) {
            return resourceNotFoundException("QueueEntry", queueEntryId);
        }
        
        public static ResourceAlreadyExistsException resourceAlreadyExistsException(String resource, String identifier) {
            return new ResourceAlreadyExistsException(resource + " already exists with " + identifier);
        }
        
        public static ResourceAlreadyExistsException customerAlreadyInQueueException(String whatsappIdentifier) {
            return resourceAlreadyExistsException("Customer", "already in queue with " + whatsappIdentifier);
        }
    }
    
    /**
     * Builds internal exceptions
     */
    public static class InternalExceptionBuilder {
        
        public static InternalException internalException(ApplicationError error, Exception cause, Object... args) {
            return new InternalException(error.getDefaultMessage(), cause, args);
        }
        
        public static InternalException internalExceptionWithReference(ApplicationError error, Object... args) {
            return new InternalException(error.getDefaultMessage(), null, args);
        }
        
        public static InternalException genericInternalException() {
            return internalExceptionWithReference(ApplicationError.INTERNAL_ERROR);
        }
        
        public static InternalException whatsAppApiException(Exception cause, String businessId) {
            return internalExceptionWithReference(ApplicationError.WHATSAPP_API_ERROR, cause, businessId);
        }
        
        public static InternalException databaseException(Exception cause, String context) {
            return internalExceptionWithReference(ApplicationError.DATABASE_ERROR, cause, context);
        }
        
        public static InternalException redisConnectionException(Exception cause) {
            return internalExceptionWithReference(ApplicationError.CACHE_ERROR, cause);
        }
        
        public static InternalException notificationFailureException(String notificationType, String whatsappIdentifier, Exception cause) {
            return internalExceptionWithReference(
                ApplicationError.NOTIFICATION_ERROR, 
                cause, 
                "Failed to send " + notificationType + " notification to " + whatsappIdentifier
            );
        }
    }
    
    /**
     * Builds queue exceptions
     */
    public static class QueueExceptionBuilder {
        
        public static QueueException queueEntryNotFoundException(Long queueEntryId) {
            return new QueueException("Queue entry not found with id: " + queueEntryId);
        }
        
        public static QueueException businessNotFoundException(Long businessId) {
            return new QueueException("Business not found with id: " + businessId);
        }
        
        public static QueueException queueClosedException(String businessName) {
            return new QueueException("Queue is closed for business: " + businessName);
        }
        
        public static QueueException customerNotInQueueException(String whatsappIdentifier) {
            return new QueueException("Customer not found in queue: " + whatsappIdentifier);
        }
        
        public static QueueException invalidQueueOperationException(String operation) {
            return new QueueException("Invalid queue operation: " + operation);
        }
    }
    
    /**
     * Builds unavailable service exceptions
     */
    public static class UnavailableServiceExceptionBuilder {
        
        public static UnavailableServiceException whatsAppServiceUnavailableException() {
            return new UnavailableServiceException("WhatsApp service is temporarily unavailable");
        }
        
        public static UnavailableServiceException databaseUnavailableException() {
            return new UnavailableServiceException("Database service is temporarily unavailable");
        }
    }
}
