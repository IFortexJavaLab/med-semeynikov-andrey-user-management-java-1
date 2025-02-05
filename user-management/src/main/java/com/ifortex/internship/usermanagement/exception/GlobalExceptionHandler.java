package com.ifortex.internship.usermanagement.exception;

import com.ifortex.internship.authserviceapi.exception.CustomFeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class
GlobalExceptionHandler {

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
}
