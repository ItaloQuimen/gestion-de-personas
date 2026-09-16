package com.imqh.personas.error;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException exception) {
        String mensaje = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return ResponseEntity.badRequest()
                .body(ErrorResponse.of(400, mensaje));
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NoSuchElementException exception) {
        return ResponseEntity.status(404)
                .body(ErrorResponse.of(404, exception.getMessage()));
    }

    @ExceptionHandler(RabbitMqUnavailableException.class)
    public ResponseEntity<ErrorResponse> handleRabbitMqUnavailable(RabbitMqUnavailableException exception) {
        return ResponseEntity.status(503)
                .body(ErrorResponse.of(503, exception.getMessage()));
    }
}
