package com.example.whatsappqueue.domain;

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
    DATABASE_ERROR("SYS_002", "system.database.error", "A database error occurred..."),
    CACHE_ERROR("SYS_003", "system.cache.error", "A cache error occurred..."),
    
    // WhatsApp API errors (custom codes)
    WHATSAPP_API_ERROR("SYS_004", "system.whatsapp.api.error", "WhatsApp API error occurred..."),
    NOTIFICATION_ERROR("SYS_005", "system.notification.error", "Failed to send notification..."),
    
    // Business rule violations (custom codes)
    QUEUE_CLOSED("SYS_006", "system.queue.closed", "Queue is currently closed..."),
    INVALID_QUEUE_OPERATION("SYS_007", "system.invalid.queue.operation", "Invalid queue operation..."),
    QUEUE_ENTRY_NOT_FOUND("SYS_008", "system.queue.entry.not.found", "Queue entry not found..."),
    BUSINESS_NOT_FOUND("SYS_009", "system.business.not.found", "Business not found...");
    
    private final String code;
    private final String messageKey;
    private final String defaultMessage;
    
    ApplicationError(String code, String messageKey, String defaultMessage) {
        this.code = code;
        this.messageKey = messageKey;
        this.defaultMessage = defaultMessage;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getMessageKey() {
        return messageKey;
    }
    
    public String getDefaultMessage() {
        return defaultMessage;
    }
}
