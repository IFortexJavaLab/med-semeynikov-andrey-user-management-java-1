package com.ifortex.internship.usermanagement.exception.usermanagement;

import com.ifortex.internship.usermanagement.exception.UserManagementException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class InternalServerException extends UserManagementException {
  public InternalServerException(String message) {
    super(message);
  }
}
