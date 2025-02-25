package com.ifortex.internship.usermanagement.controller;

import com.ifortex.internship.usermanagement.service.UserService;
import com.ifortex.internship.usermanagementapi.dto.request.UpdateUserDto;
import com.ifortex.internship.usermanagementapi.dto.response.FullUserDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/account")
@RequiredArgsConstructor
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "User Account", description = "User account management")
public class UserController {

  private final UserService userService;

  @Operation(summary = "Update user", description = "Allows updating user information.")
  @PatchMapping()
  public ResponseEntity<?> updateUser(@RequestBody @Valid UpdateUserDto updateUserDto) {
    UpdateUserDto updatedUser = userService.updateUser(updateUserDto);
    return ResponseEntity.ok().body(updatedUser);
  }

  @Operation(
      summary = "Get current user profile",
      description = "Retrieve detailed user information by authentication.")
  @GetMapping()
  public ResponseEntity<FullUserDto> getUserByAuth() {
    FullUserDto userProfile = userService.getUserProfileByAuth();
    return ResponseEntity.ok(userProfile);
  }
}
