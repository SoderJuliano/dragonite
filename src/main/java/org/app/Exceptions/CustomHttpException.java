package org.app.Exceptions;

public class CustomHttpException extends RuntimeException {
    private final int statusCode;

    public CustomHttpException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
