package com.management.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = ChangePasswordException.class)
    public ResponseEntity handleChangePasswordException(ChangePasswordException ex) {
        return new ResponseEntity<>(body(ex.getMessage()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = UnauthorizedOperationException.class)
    public ResponseEntity handleUnauthorizedOperationException(UnauthorizedOperationException ex) {
        return new ResponseEntity<>(body(ex.getMessage()), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(value = UserNotFoundException.class)
    public ResponseEntity handleUserNotFoundException(UserNotFoundException ex) {
        return new ResponseEntity<>(body(ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity handleValidationExceptions(ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations().forEach((error) -> {
            String fieldName = error.getPropertyPath().toString();
            String errorMessage = error.getMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(body(errors.toString()), HttpStatus.BAD_REQUEST);
    }

    private Map<String, String> body(String message) {
        Map<String, String> map = new HashMap<>();
        map.put("message", message);
        return map;
    }
}
