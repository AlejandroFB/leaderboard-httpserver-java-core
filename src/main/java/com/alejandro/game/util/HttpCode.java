package com.alejandro.game.util;

/**
 * Http codes used in responses.
 *
 * @author afernandez
 */
public enum HttpCode {
    OK(200),
    BAD_REQUEST(400),
    UNAUTHORIZED(401),
    NOT_FOUND(404),
    SERVER_ERROR(500);

    int code;

    HttpCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}