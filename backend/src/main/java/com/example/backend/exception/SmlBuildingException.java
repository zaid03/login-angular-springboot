package com.example.backend.exception;

/**
 * Exception thrown when there is an error building the SML (Service Markup Language) input.
 * This exception is used during the construction of XML payloads for SICAL web service requests.
 */
public class SmlBuildingException extends Exception {
    
    /**
     * Constructs a new SmlBuildingException with the specified detail message.
     *
     * @param message the detail message explaining the error during SML building
     */
    public SmlBuildingException(String message) {
        super(message);
    }
    
    /**
     * Constructs a new SmlBuildingException with the specified detail message and cause.
     *
     * @param message the detail message explaining the error during SML building
     * @param cause the underlying exception that caused this error
     */
    public SmlBuildingException(String message, Throwable cause) {
        super(message, cause);
    }
}
