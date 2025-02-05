package com.ifortex.internship.usermanagement.exception.usermanagement;

import com.ifortex.internship.usermanagement.exception.UserManagementException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class EntityNotFoundException extends UserManagementException {
  public EntityNotFoundException(String message) {
    super(message);
  }
}
