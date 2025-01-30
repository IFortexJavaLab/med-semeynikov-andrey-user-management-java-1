package com.ifortex.internship.usermanagement.service;

import com.ifortex.internship.usermanagementapi.dto.request.AuthUserForUserManagementDto;
import com.ifortex.internship.usermanagementapi.dto.request.UpdateUserDto;
import com.ifortex.internship.usermanagementapi.dto.response.FullUserDto;
import com.ifortex.internship.usermanagementapi.dto.response.SuccessResponse;
import com.ifortex.internship.usermanagementapi.dto.response.UserListViewDto;
import java.util.List;

public interface UserService {

  // todo add javadoc
  List<UserListViewDto> getAllUsers();

  SuccessResponse saveUserFromAuthService(AuthUserForUserManagementDto authUserDto);

  UpdateUserDto updateUserByAuthentication(UpdateUserDto updateUserDto);

  FullUserDto getFullUserForAdmin(String userId);
}
