package com.example.whatsappqueue.common.exception;

public class QueueException extends RuntimeException {

    public QueueException(String message) {
        super(message);
    }

    public QueueException(String message, Throwable cause) {
        super(message, cause);
    }

    public QueueException(String message, String field, Object value) {
        super(String.format(message, field, value));
    }
}
