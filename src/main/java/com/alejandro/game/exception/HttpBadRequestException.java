package com.alejandro.game.exception;

/**
 * Exception thrown when the request exists but there is comething wrong in the request, like wrong Http method
 * or no content body in a POST request.
 *
 * @author afernandez
 */
public class HttpBadRequestException extends Exception {

    public HttpBadRequestException(String message) {
        super(message);
    }

    public HttpBadRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}