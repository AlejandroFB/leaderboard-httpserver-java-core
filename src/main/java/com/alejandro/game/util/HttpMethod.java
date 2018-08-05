package com.alejandro.game.util;

/**
 * Http request methods.
 *
 * @author afernandez
 */
public enum HttpMethod {
    GET("GET"),
    POST("POST");

    private String method;

    HttpMethod(String method) {
        this.method = method;
    }

    public String getMethod() {
        return method;
    }
}