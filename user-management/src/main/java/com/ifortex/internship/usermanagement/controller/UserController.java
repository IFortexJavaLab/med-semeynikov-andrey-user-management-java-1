package com.ifortex.internship.usermanagement.controller;

import com.ifortex.internship.usermanagement.service.UserService;
import com.ifortex.internship.usermanagementapi.dto.request.UpdateUserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/account")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;
  
  @PatchMapping()
  public ResponseEntity<?> updateUserByAuthentication(@RequestBody UpdateUserDto updateUserDto) {
    var updatedUser = userService.updateUserByAuthentication(updateUserDto);
    return ResponseEntity.ok().body(updatedUser);
  }
  
  //todo add endpoint getUserDetails for user

}
