package org.app.config.handlers;

import com.mongodb.DuplicateKeyException;
import org.app.Exceptions.*;
import org.app.model.common.DefaultAnswer;
import org.app.utils.LocalLog;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

import static org.app.config.EmojiParser.parseEmojis;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        LocalLog.logErr(":skull An unexpected runtime error occurred");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(parseEmojis(":skull An unexpected runtime error occurred"));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Object> handleNotFoundException(NotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new DefaultAnswer(exception));
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Object> handleUnauthorizedException(UnauthorizedException exception) {
        return ResponseEntity.status(401).body(new DefaultAnswer(exception));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException exception) {
        return ResponseEntity.status(422).body(new DefaultAnswer(exception));
    }

    @ExceptionHandler(DuplicateKeyException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<Object> handleDuplicateKeyException(DuplicateKeyException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT.value()).body(new DefaultAnswer(ex));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<String> handleBadRequestException(BadRequestException exception) {
        LocalLog.logErr(":lock "+ exception.getMessage());
        Map<String, String> errors = new HashMap<>();
        errors.put("message", exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(parseEmojis(":lock "+ exception.getMessage()));
    }

    @ExceptionHandler(NoPasswordException.class)
    public ResponseEntity<DefaultAnswer> handleNoPasswordException(NoPasswordException exception) {
        LocalLog.logErr(":lock "+ exception.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT.value()).body(new DefaultAnswer(exception));
    }

    @ExceptionHandler(CustomHttpException.class)
    public ResponseEntity<DefaultAnswer> handleCustomHttpException(CustomHttpException ex) {
        DefaultAnswer errorResponse = new DefaultAnswer(ex);
        return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(ex.getStatusCode()));
    }
}
