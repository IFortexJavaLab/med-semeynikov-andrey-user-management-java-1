package com.ifortex.internship.usermanagement.exception;

import com.ifortex.internship.authserviceapi.exception.CustomFeignException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  @ExceptionHandler(CustomFeignException.class)
  public ResponseEntity<String> handleCustomFeignException(CustomFeignException ex) {
    HttpStatus status = HttpStatus.valueOf(ex.getStatusCode());

    // feature get original message
    log.info("Error occurred: {}", ex.getMessage());

    return new ResponseEntity<>(ex.getMessage(), status);
  }

  @ExceptionHandler(UserManagementException.class)
  public ResponseEntity<String> handleUserManagementException(UserManagementException ex) {

    ResponseStatus statusAnnotation = ex.getClass().getAnnotation(ResponseStatus.class);
    HttpStatus status =
        statusAnnotation != null ? statusAnnotation.value() : HttpStatus.INTERNAL_SERVER_ERROR;

    return new ResponseEntity<>(ex.getMessage(), status);
  }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {

        log.debug(ex.getMessage());

        BindingResult bindingResult = ex.getBindingResult();

        Map<String, String> errors = new HashMap<>();
        bindingResult
                .getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> constraintViolationException(ConstraintViolationException ex, WebRequest request) {
        log.debug(ex.getMessage());

        Map<String, String> errors = new HashMap<>();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            errors.put(violation.getPropertyPath().toString(), violation.getMessage());
        }
        return ResponseEntity.badRequest().body(errors);
    }
}
