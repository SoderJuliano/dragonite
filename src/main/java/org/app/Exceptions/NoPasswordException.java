package org.app.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.PARTIAL_CONTENT)
public class NoPasswordException extends RuntimeException {

    public NoPasswordException(String message) {
        super(message);
    }
}
