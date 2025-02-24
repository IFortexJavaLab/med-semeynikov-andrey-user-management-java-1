package com.ifortex.internship.usermanagementapi;


import com.ifortex.internship.usermanagementapi.config.FeignClientConfiguration;
import com.ifortex.internship.usermanagementapi.dto.request.AuthUserForUserManagementDto;
import com.ifortex.internship.usermanagementapi.dto.request.DeleteUserRequest;
import com.ifortex.internship.usermanagementapi.dto.response.SuccessResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
    name = "user-management",
    path = "/api/v1/user-management/auth",
    configuration = FeignClientConfiguration.class)
public interface UserManagementApi {

  @PostMapping("/save-user")
  ResponseEntity<SuccessResponse> saveUser(@RequestBody AuthUserForUserManagementDto authUserForUserManagementDto);

  @DeleteMapping("/delete-user")
  ResponseEntity<Void> deleteUser(@RequestBody DeleteUserRequest request);
}
