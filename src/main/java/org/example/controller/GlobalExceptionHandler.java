package org.example.controller;

import org.example.exception.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        logger.warn("Illegal argument: {}", e.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                e.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException e) {
        logger.warn("User not found: {}", e.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                e.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExistsException(UserAlreadyExistsException e) {
        logger.warn("User already exists: {}", e.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                e.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException e) {
        logger.warn("Validation error: {}", e.getMessage());
        Map<String, Object> errors = new HashMap<>();
        errors.put("status", HttpStatus.BAD_REQUEST.value());
        errors.put("timestamp", LocalDateTime.now());
        Map<String, String> fieldErrors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error ->
                fieldErrors.put(error.getField(), error.getDefaultMessage())
        );
        errors.put("errors", fieldErrors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception e) {
        logger.error("Unexpected error occurred", e);
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An unexpected error occurred",
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    public static class ErrorResponse {
        private final int status;
        private final String message;
        private final LocalDateTime timestamp;

        public ErrorResponse(int status, String message, LocalDateTime timestamp) {
            this.status = status;
            this.message = message;
            this.timestamp = timestamp;
        }

        public int getStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }
    }
}
