package com.example.whatsappqueue.application;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class QueueLoggingService {
    
    /**
     * Logs customer join queue operation
     */
    public void logCustomerJoinedQueue(String businessId, String whatsappIdentifier, Integer position) {
        log.info("Customer joined queue - businessId: {}, whatsappIdentifier: {}, position: {}", 
                businessId, whatsappIdentifier, position);
    }
    
    /**
     * Logs customer leave queue operation
     */
    public void logCustomerLeftQueue(String businessId, String whatsappIdentifier) {
        log.info("Customer left queue - businessId: {}, whatsappIdentifier: {}", 
                businessId, whatsappIdentifier);
    }
    
    /**
     * Logs queue position update
     */
    public void logQueuePositionUpdate(String businessId, String whatsappIdentifier, Integer oldPosition, Integer newPosition) {
        log.info("Queue position updated - businessId: {}, whatsappIdentifier: {}, oldPosition: {}, newPosition: {}", 
                businessId, whatsappIdentifier, oldPosition, newPosition);
    }
    
    /**
     * Logs WhatsApp message processing
     */
    public void logWhatsAppMessageProcessed(String businessId, String messageType, String fromNumber) {
        log.info("WhatsApp message processed - businessId: {}, messageType: {}, fromNumber: {}", 
                businessId, messageType, fromNumber);
    }
    
    /**
     * Logs queue advancement operation
     */
    public void logQueueAdvancement(String businessId, String servedCustomerIdentifier, String action) {
        log.info("Queue advanced - businessId: {}, servedCustomer: {}, action: {}", 
                businessId, servedCustomerIdentifier, action);
    }
    
    /**
     * Logs validation errors
     */
    public void logValidationError(String businessId, String operation, String errorMessage) {
        log.warn("Validation error - businessId: {}, operation: {}, error: {}", 
                businessId, operation, errorMessage);
    }
    
    /**
     * Logs queue status change
     */
    public void logQueueStatusChange(String businessId, String whatsappIdentifier, String oldStatus, String newStatus) {
        log.info("Queue status changed - businessId: {}, whatsappIdentifier: {}, oldStatus: {}, newStatus: {}", 
                businessId, whatsappIdentifier, oldStatus, newStatus);
    }
    
    /**
     * Logs notification sent
     */
    public void logNotificationSent(String businessId, String whatsappIdentifier, String notificationType) {
        log.info("Notification sent - businessId: {}, whatsappIdentifier: {}, type: {}", 
                businessId, whatsappIdentifier, notificationType);
    }
    
    /**
     * Logs notification failure
     */
    public void logNotificationFailure(String businessId, String whatsappIdentifier, String notificationType, String error) {
        log.error("Notification failed - businessId: {}, whatsappIdentifier: {}, type: {}, error: {}", 
                businessId, whatsappIdentifier, notificationType, error);
    }
    
    /**
     * Logs system operations with context
     */
    public void logSystemOperation(String operation, String context) {
        log.info("System operation - operation: {}, context: {}", operation, context);
    }
}
