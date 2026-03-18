package com.example.backend.exception;

/**
 * Exception thrown when SML (Strict Markup Language) processing fails,
 * including generation, parsing, or SOAP communication with SICAL web service.
 */
public class SmlProcessingException extends RuntimeException {
    
    public SmlProcessingException(String message) {
        super(message);
    }
    
    public SmlProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public SmlProcessingException(Throwable cause) {
        super(cause);
    }
}
