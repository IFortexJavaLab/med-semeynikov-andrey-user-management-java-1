package com.ifortex.internship.usermanagement.exception.auth;

import com.ifortex.internship.usermanagement.exception.UserManagementException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class AuthorizationException extends UserManagementException {
  public AuthorizationException(String message) {
    super(message);
  }
}
