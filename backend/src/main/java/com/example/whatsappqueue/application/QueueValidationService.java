package com.example.whatsappqueue.application;

import com.example.whatsappqueue.domain.QueueEntry;
import com.example.whatsappqueue.domain.Business;
import com.example.whatsappqueue.common.exception.ExceptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class QueueValidationService {
    
    private static final Pattern WHATAPP_PHONE_PATTERN = Pattern.compile("^\\+[1-9]\\d{1,14}$");
    private static final Pattern WHATSAPP_IDENTIFIER_PATTERN = Pattern.compile("^[+]?[1-9]\\d{1,14}(@c\\.us)?$");
    
    // No final fields needed for this service
    
    /**
     * Validates if a customer can join a specific business queue
     */
    public void validateCustomerCanJoinQueue(Business business, String whatsappIdentifier) {
        if (business == null) {
            throw ExceptionService.ValidationExceptionBuilder.validationException("Business cannot be null");
        }
        
        if (!Boolean.TRUE.equals(business.getQueueOpen())) {
            throw ExceptionService.ValidationExceptionBuilder.queueClosedException();
        }
        
        if (!isValidWhatsAppIdentifier(whatsappIdentifier)) {
            throw ExceptionService.ValidationExceptionBuilder.invalidWhatsAppFormatException();
        }
    }
    
    /**
     * Validates if a customer is already in the queue for the same business
     */
    public void validateCustomerNotAlreadyInQueue(QueueEntry existingEntry) {
        if (existingEntry != null && existingEntry.getStatus() == QueueEntry.Status.ACTIVE) {
            throw ExceptionService.ValidationExceptionBuilder.customerAlreadyInQueueException(existingEntry.getWhatsappIdentifier());
        }
    }
    
    /**
     * Validates queue position constraints
     */
    public void validateQueuePosition(Integer position) {
        if (position == null || position < 1) {
            throw ExceptionService.ValidationExceptionBuilder.invalidQueuePositionException();
        }
    }
    
    /**
     * Validates business service time constraints
     */
    public void validateBusinessServiceTime(Integer serviceTimeMinutes) {
        if (serviceTimeMinutes == null || serviceTimeMinutes < 1 || serviceTimeMinutes > 120) {
            throw ExceptionService.ValidationExceptionBuilder.invalidBusinessServiceTimeException();
        }
    }
    
    /**
     * Validates business notification threshold constraints
     */
    public void validateNotificationThreshold(Integer notificationThreshold) {
        if (notificationThreshold == null || notificationThreshold < 1 || notificationThreshold > 10) {
            throw ExceptionService.ValidationExceptionBuilder.invalidNotificationThresholdException();
        }
    }
    
    /**
     * Validates business name constraints
     */
    public void validateBusinessName(String name) {
        if (name == null || name.trim().isEmpty() || name.length() > 255) {
            throw ExceptionService.ValidationExceptionBuilder.invalidBusinessNameException();
        }
    }
    
    /**
     * Validates WhatsApp phone number format for business
     */
    public void validateBusinessWhatsAppNumber(String whatsappPhoneNumber) {
        if (whatsappPhoneNumber == null || !WHATAPP_PHONE_PATTERN.matcher(whatsappPhoneNumber).matches()) {
            throw ExceptionService.ValidationExceptionBuilder.invalidWhatsAppFormatException();
        }
    }
    
    /**
     * Validates if a customer can leave the queue
     */
    public void validateCustomerCanLeaveQueue(QueueEntry queueEntry) {
        if (queueEntry == null) {
            throw ExceptionService.ValidationExceptionBuilder.validationException("Queue entry cannot be null");
        }
        
        if (queueEntry.getStatus() != QueueEntry.Status.ACTIVE) {
            throw ExceptionService.ValidationExceptionBuilder.validationExceptionWithMessage(
                "Customer cannot leave queue: not currently active (status: " + queueEntry.getStatus() + ")");
        }
    }
    
    /**
     * Validates queue advancement operation
     */
    public void validateQueueAdvancement(Business business, QueueEntry currentEntry) {
        if (business == null) {
            throw ExceptionService.ValidationExceptionBuilder.validationException("Business cannot be null");
        }
        
        if (currentEntry == null) {
            throw ExceptionService.ValidationExceptionBuilder.validationException("No current entry to advance");
        }
        
        if (currentEntry.getStatus() != QueueEntry.Status.ACTIVE) {
            throw ExceptionService.ValidationExceptionBuilder.validationExceptionWithMessage(
                "Cannot advance non-active entry (status: " + currentEntry.getStatus() + ")"
            );
        }
    }
    
    private boolean isValidWhatsAppIdentifier(String identifier) {
        return WHATSAPP_IDENTIFIER_PATTERN.matcher(identifier).matches();
    }
}
