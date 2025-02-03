package com.ifortex.internship.usermanagement.controller;

import com.ifortex.internship.usermanagement.service.UserService;
import com.ifortex.internship.usermanagementapi.dto.request.UpdateUserDto;
import com.ifortex.internship.usermanagementapi.dto.response.FullUserDto;
import com.ifortex.internship.usermanagementapi.dto.response.UserListViewDto;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/accounting/users")
@RequiredArgsConstructor
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "Admin functions", description = "User account management by admins")
public class AdminController {

  private final UserService userService;

  // todo admin functionality for update user details
  @PatchMapping()
  public ResponseEntity<?> updateUserByAdmin(@RequestBody UpdateUserDto updateUserDto) {
    return null;
  }

  @GetMapping()
  public ResponseEntity<?> getAllUsers() {
    List<UserListViewDto> users = userService.getAllUsers();
    return ResponseEntity.ok(users);
  }

  @GetMapping("/{userId}")
  public ResponseEntity<FullUserDto> getUserByUserId(@PathVariable String userId) {
    var fullUser = userService.getFullUserData(userId);
    return ResponseEntity.ok(fullUser);
  }

}
