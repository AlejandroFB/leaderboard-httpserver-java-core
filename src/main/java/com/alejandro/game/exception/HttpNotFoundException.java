package com.alejandro.game.exception;

/**
 * Exception thrown when the url does not exist in the application.
 *
 * @author afernandez
 */
public class HttpNotFoundException extends Exception {

    public HttpNotFoundException(String message) {
        super(message);
    }

    public HttpNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}