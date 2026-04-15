package com.example.microservice.exception;

import com.example.microservice.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Centralised exception handling for the entire API.
 * Converts exceptions into structured {@link ErrorResponse} objects
 * with appropriate HTTP status codes.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /* ---- 404 Not Found ---- */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                LocalDateTime.now(),
                null
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /* ---- 400 Bad Request – bean-validation failures ---- */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        List<String> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .toList();
        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Validation failed",
                LocalDateTime.now(),
                fieldErrors
        );
        return ResponseEntity.badRequest().body(error);
    }

    /* ---- 400 Bad Request – malformed JSON / date format ---- */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadable(HttpMessageNotReadableException ex) {
        String message = "Malformed request body";
        Throwable cause = ex.getCause();
        if (cause instanceof com.fasterxml.jackson.databind.exc.InvalidFormatException ife) {
            if (LocalDate.class.isAssignableFrom(ife.getTargetType())) {
                message = "Invalid date format. Expected format: YYYY-MM-DD";
            }
        }
        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                message,
                LocalDateTime.now(),
                null
        );
        return ResponseEntity.badRequest().body(error);
    }

    /* ---- 400 Bad Request – query-param type mismatch (e.g. bad date) ---- */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String message = String.format("Invalid value '%s' for parameter '%s'", ex.getValue(), ex.getName());
        if (ex.getRequiredType() != null && LocalDate.class.isAssignableFrom(ex.getRequiredType())) {
            message += ". Expected format: YYYY-MM-DD";
        }
        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                message,
                LocalDateTime.now(),
                null
        );
        return ResponseEntity.badRequest().body(error);
    }

    /* ---- 500 Internal Server Error – catch-all ---- */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An unexpected error occurred",
                LocalDateTime.now(),
                null
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
