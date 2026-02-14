package com.example.whatsappqueue.common.exception;

public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ValidationException(String field, String value) {
        super(String.format("Validation failed for %s: '%s'", field, value));
    }
}
