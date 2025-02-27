package com.ifortex.internship.usermanagement.controller;

import com.ifortex.internship.usermanagement.service.UserService;
import com.ifortex.internship.usermanagementapi.dto.request.UpdateUserDto;
import com.ifortex.internship.usermanagementapi.dto.request.UserSearchRequest;
import com.ifortex.internship.usermanagementapi.dto.response.FullUserDto;
import com.ifortex.internship.usermanagementapi.dto.response.UserListViewDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@Slf4j
@RestController
@RequestMapping("/api/v1/accounting/users")
@RequiredArgsConstructor
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "Admin functions", description = "User account management by admins")
public class AdminController {

  private final UserService userService;


  @Operation(
      summary = "Search users",
      description = "Allows admins to search users with filters and pagination.")
  @PostMapping("/search")
  public ResponseEntity<?> searchUsers(
          @RequestParam(defaultValue = "0") @Min(value = 0, message = "Page can't be a negative number")
          int page,
          @RequestParam(defaultValue = "20")
          @Min(value = 1, message = "Size of the page can't be less than 1")
          @Max(value = 100, message = "Size of the page can't be more than 100")
          int size,
      @RequestBody UserSearchRequest request) {
    Page<UserListViewDto> result = userService.searchUsers(request, page, size);
    return ResponseEntity.ok(result.getContent());
  }

  @Operation(summary = "Update user", description = "Allows admins to update user information.")
  @PatchMapping("{userId}")
  public ResponseEntity<?> updateUserByAdmin(
      @PathVariable("userId") String userId, @RequestBody UpdateUserDto updateUserDto) {
    UpdateUserDto updatedUser = userService.updateUserByAdmin(userId, updateUserDto);
    return ResponseEntity.ok(updatedUser);
  }

  @Operation(
      summary = "Get user by ID",
      description = "Retrieve detailed user information by user ID.")
  @GetMapping("/{userId}")
  public ResponseEntity<FullUserDto> getUserByUserId(@PathVariable String userId) {
    var fullUser = userService.getUserProfileById(userId);
    return ResponseEntity.ok(fullUser);
  }
}
