package org.app.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.DESTINATION_LOCKED)
public class IAException extends RuntimeException {

    public IAException(String message) {
        super(message);
    }
}
