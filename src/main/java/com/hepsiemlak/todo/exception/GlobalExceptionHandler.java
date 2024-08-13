package com.hepsiemlak.todo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author suleyman.yildirim
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUserNotFoundException(UserNotFoundException ex) {
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("error", ex.getErrorCode().getCode());
        errorDetails.put("message", ex.getMessage());
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserExistsException.class)
    public ResponseEntity<Map<String, Object>> handleUserExistsException(UserExistsException ex) {
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("error", ex.getErrorCode().getCode());
        errorDetails.put("message", ex.getMessage());
        return new ResponseEntity<>(errorDetails, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleTaskNotFoundException(TaskNotFoundException ex) {
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("error", ex.getErrorCode().getCode());
        errorDetails.put("message", ex.getMessage());
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("error", "Invalid user input");

        List<String> errorMessages = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .toList();

        errorDetails.put("message", errorMessages);
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

}

