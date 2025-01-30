package com.ifortex.internship.usermanagement.controller;

import com.ifortex.internship.usermanagement.service.UserService;
import com.ifortex.internship.usermanagementapi.dto.request.AuthUserForUserManagementDto;
import com.ifortex.internship.usermanagementapi.dto.response.SuccessResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("api/v1/user-management/auth")
@RequiredArgsConstructor
public class AuthController {

  private final UserService userService;

  @PostMapping("/save-user")
  public ResponseEntity<SuccessResponse> saveUserFromAuth(@RequestBody AuthUserForUserManagementDto authUserDto) {

    log.debug("Attempt to save user: {} from auth service", authUserDto.getUserId());

    SuccessResponse response = userService.saveUserFromAuthService(authUserDto);
    return ResponseEntity.ok(response);
  }
}
