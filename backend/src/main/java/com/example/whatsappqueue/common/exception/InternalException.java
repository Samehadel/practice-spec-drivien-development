package com.example.whatsappqueue.common.exception;

public class InternalException extends RuntimeException {

    public InternalException(String message) {
        super(message);
    }

    public InternalException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public InternalException(String message, Throwable cause, Object... args) {
        super(message, cause);
    }
}
