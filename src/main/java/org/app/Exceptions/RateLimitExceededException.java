package org.app.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.TOO_MANY_REQUESTS, reason = "Rate limit exceeded")
public class RateLimitExceededException extends BadRequestException {
    public RateLimitExceededException(String message) {
        super(message);
    }
}
