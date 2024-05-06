package org.app.Exceptions;

public class UnauthorizedException extends RuntimeException {

    private String message;

    public UnauthorizedException(String message) {
        super();
        this.message = message;
    }

    public UnauthorizedException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public String getMessage() {
        return this.message;
    }

}
