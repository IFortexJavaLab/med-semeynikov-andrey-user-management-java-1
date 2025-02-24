package com.ifortex.internship.usermanagement.controller;

import com.ifortex.internship.usermanagement.service.UserService;
import com.ifortex.internship.usermanagementapi.dto.request.AuthUserForUserManagementDto;
import com.ifortex.internship.usermanagementapi.dto.request.DeleteUserRequest;
import com.ifortex.internship.usermanagementapi.dto.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("api/v1/user-management/auth")
@RequiredArgsConstructor
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "Auth service")
public class ServiceAuthController {

  private final UserService userService;

  @Operation(
      summary = "Save user from Auth service",
      description =
          "Saves user details received from the authentication service into the User Management system.")
  @PostMapping("/save-user")
  public ResponseEntity<SuccessResponse> saveUserFromAuth(
      @RequestBody AuthUserForUserManagementDto authUserDto) {

    log.debug("Attempt to save user: {} from auth service", authUserDto.getUserId());

    userService.saveUserFromAuthService(authUserDto);
    return ResponseEntity.noContent().build();
  }

  @Operation(
      summary = "Delete user entirely",
      description = "Delete user entirely from user management service")
  @DeleteMapping("/delete-user")
  public ResponseEntity<Void> saveUserFromAuth(@RequestBody DeleteUserRequest request) {

    log.debug("Attempt to delete user: {} from auth service", request.getUserId());
    userService.deleteUser(request);
    return ResponseEntity.noContent().build();
  }
}
