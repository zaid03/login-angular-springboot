package com.example.backend.exception;

/**
 * Custom exception for XML parsing errors during document builder initialization and parsing.
 * 
 * This exception is thrown when there are issues with:
 * - Initializing the DocumentBuilder with security features
 * - Parsing XML responses from web services
 * - Configuring XML parser factories
 */
public class XmlParsingException extends Exception {
    
    /**
     * Constructs an XmlParsingException with the specified detail message.
     * 
     * @param message the detail message explaining the parsing error
     */
    public XmlParsingException(String message) {
        super(message);
    }
    
    /**
     * Constructs an XmlParsingException with the specified detail message and cause.
     * 
     * @param message the detail message explaining the parsing error
     * @param cause the underlying exception that caused this error
     */
    public XmlParsingException(String message, Throwable cause) {
        super(message, cause);
    }
}
