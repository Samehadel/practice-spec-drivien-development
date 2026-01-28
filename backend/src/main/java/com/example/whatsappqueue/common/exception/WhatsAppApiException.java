package com.example.whatsappqueue.common.exception;

public class WhatsAppApiException extends RuntimeException {

    public WhatsAppApiException(String message) {
        super(message);
    }

    public WhatsAppApiException(String message, Throwable cause) {
        super(message, cause);
    }

    public WhatsAppApiException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    private int statusCode;

    public int getStatusCode() {
        return statusCode;
    }
}
